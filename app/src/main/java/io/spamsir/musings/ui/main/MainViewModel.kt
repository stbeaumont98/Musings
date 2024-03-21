package io.spamsir.musings.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import dagger.hilt.android.lifecycle.HiltViewModel
import io.spamsir.musings.data.database.NoteDatabase
import io.spamsir.musings.data.database.NoteDatabaseDao
import io.spamsir.musings.data.domain.Event
import io.spamsir.musings.data.domain.Note
import io.spamsir.musings.ui.listitems.NoteEvent
import io.spamsir.musings.ui.main.states.AllNotesState
import io.spamsir.musings.ui.main.states.FavoritesState
import io.spamsir.musings.ui.main.states.HomeState
import io.spamsir.musings.ui.main.states.MainState
import io.spamsir.musings.ui.main.states.SearchState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class MainViewModel @Inject constructor(
    private val noteDao: NoteDatabaseDao
): ViewModel() {
    private val mutableState = MutableStateFlow(MainState())
    val state = mutableState.asStateFlow()

    fun loadData() {
        viewModelScope.launch(Dispatchers.IO) {
            mutableState.value = mutableState.value.copy(
                searchState = SearchState(
                    allNotes = noteDao.getAllNotes(),
                    searchQuery = state.value.searchState.searchQuery,
                    isSearching = state.value.searchState.isSearching,
                    notesList = noteDao.getAllNotes().filter { note -> note.title.uppercase().contains(state.value.searchState.searchQuery.trim().uppercase()) ||
                            note.content.uppercase().contains(state.value.searchState.searchQuery.trim().uppercase()) }
                ),
                homeState = HomeState(
                    noteToday = getNoteToday(),
                    recentNotes = recentNotes(),
                    rFavNotes = recentNotes(true)
                ),
                allNotesState = AllNotesState(
                    allNotes = noteDao.getAllNotes()
                ),
                favoritesState = FavoritesState(
                    favNotes = noteDao.getLikedNotes()
                )
            )
        }
    }

    private fun onSearchQueryChange(text : String) {
        mutableState.value = mutableState.value.copy(
            searchState = SearchState(
                allNotes = mutableState.value.searchState.allNotes,
                searchQuery = text,
                isSearching = mutableState.value.searchState.isSearching,
                notesList = mutableState.value.searchState.notesList
            )
        )
    }

    private fun onToggleSearch() {
        mutableState.value = mutableState.value.copy(
            searchState = SearchState(
                allNotes = mutableState.value.searchState.allNotes,
                searchQuery = mutableState.value.searchState.searchQuery,
                isSearching = !mutableState.value.searchState.isSearching,
                notesList = mutableState.value.searchState.notesList
            )
        )
        if (!mutableState.value.searchState.isSearching) {
            onSearchQueryChange("")
        }
    }

    private fun getNoteToday() : Note {
        val start = Calendar.getInstance()
        start.set(Calendar.HOUR_OF_DAY, 0)
        start.set(Calendar.MINUTE, 0)
        return noteDao.getNoteFromDate(start.timeInMillis)
    }

    private fun recentNotes(isFav: Boolean = false) : List<Note> {
        val start = Calendar.getInstance()
        start.set(Calendar.HOUR_OF_DAY, 0)
        start.set(Calendar.MINUTE, 0)
        return if (isFav)
            noteDao.getRecentLikedNotes(start.timeInMillis)
        else
            noteDao.getRecentNotes(start.timeInMillis)
    }

    fun onEvent(event: Event) {
        viewModelScope.launch(Dispatchers.IO) {
            when (event) {
                is MainEvent.OnQueryChange -> onSearchQueryChange(event.text)
                is MainEvent.OnToggle -> onToggleSearch()
                is NoteEvent.UpdateNote -> noteDao.update(event.key, event.isLiked)
            }
            loadData()
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
                val application = checkNotNull(extras[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY])
                val dataSource = NoteDatabase.getInstance(application.applicationContext, CoroutineScope(
                    Dispatchers.IO)
                ).noteDatabaseDao

                return MainViewModel(dataSource) as T
            }
        }
    }
}