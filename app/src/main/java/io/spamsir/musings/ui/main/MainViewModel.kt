package io.spamsir.musings.ui.main

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import dagger.hilt.android.lifecycle.HiltViewModel
import io.spamsir.musings.data.database.NoteDatabase
import io.spamsir.musings.data.database.NoteDatabaseDao
import io.spamsir.musings.data.domain.Event
import io.spamsir.musings.ui.listitems.NoteEvent
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
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
                allNotes = noteDao.getAllNotes(),
                notesList = noteDao.getAllNotes().filter { note -> note.title.uppercase().contains(state.value.searchQuery.trim().uppercase()) ||
                        note.content.uppercase().contains(state.value.searchQuery.trim().uppercase()) }
            )
        }
    }

    private fun onSearchQueryChange(text : String) {
        mutableState.value = mutableState.value.copy(searchQuery = text)
    }

    private fun onToggleSearch() {
        mutableState.value = mutableState.value.copy(
            isSearching = !mutableState.value.isSearching
        )
        if (!mutableState.value.isSearching) {
            onSearchQueryChange("")
        }
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