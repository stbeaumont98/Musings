package io.spamsir.musings.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import dagger.hilt.android.lifecycle.HiltViewModel
import io.spamsir.musings.allnotes.AllNotesState
import io.spamsir.musings.database.NoteDatabase
import io.spamsir.musings.database.NoteDatabaseDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class AllNotesViewModel @Inject constructor(
    private val noteDao: NoteDatabaseDao
): ViewModel() {

    private val mutableState = MutableStateFlow(AllNotesState())
    val state = mutableState.asStateFlow()

    fun loadData() {
        viewModelScope.launch(Dispatchers.IO) {
            mutableState.value = mutableState.value.copy(allNotes = noteDao.getAllNotes())
        }
    }

    companion object {
        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
                val application = checkNotNull(extras[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY])
                val dataSource = NoteDatabase.getInstance(application.applicationContext, CoroutineScope(Dispatchers.IO)).noteDatabaseDao

                return AllNotesViewModel(dataSource) as T
            }
        }
    }
}