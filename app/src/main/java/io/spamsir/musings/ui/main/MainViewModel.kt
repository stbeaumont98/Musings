package io.spamsir.musings.ui.main

import android.util.Log
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import dagger.hilt.android.lifecycle.HiltViewModel
import io.spamsir.musings.data.database.NoteDatabase
import io.spamsir.musings.data.database.NoteDatabaseDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.combine
import kotlinx.coroutines.flow.stateIn
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
                notesList = state.value.allNotes.filter { note -> note.title.uppercase().contains(state.value.searchQuery.trim().uppercase()) ||
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

    fun onEvent(event: MainEvent) {
        viewModelScope.launch {
            when (event) {
                is MainEvent.OnQueryChange -> onSearchQueryChange(event.text)
                is MainEvent.OnToggle -> onToggleSearch()
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