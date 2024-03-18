package io.spamsir.musings.ui.newnote

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import dagger.hilt.android.lifecycle.HiltViewModel
import io.spamsir.musings.data.domain.Note
import io.spamsir.musings.data.domain.Settings
import io.spamsir.musings.data.database.SettingsManager
import io.spamsir.musings.data.database.NoteDatabase
import io.spamsir.musings.data.database.NoteDatabaseDao
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class NewNoteViewModel @Inject constructor(
    private val settingsManager: SettingsManager,
    private val noteDao: NoteDatabaseDao
): ViewModel() {

    private val mutableState = MutableStateFlow(NewNoteState())
    val state = mutableState.asStateFlow()

    fun loadData() {
        viewModelScope.launch(Dispatchers.IO) {
            mutableState.value = mutableState.value.copy(
                noteToday = getNoteToday(),
                settings = settingsManager.getSettings().stateIn(viewModelScope).value
            )
        }
    }

    fun onEvent(event: NewNoteEvent) {
        when(event) {
            is NewNoteEvent.SaveSettings -> { saveSettings(event.settings) }
            is NewNoteEvent.SaveNote -> { noteDao.insert(event.note) }
        }
        loadData()
    }

    private fun saveSettings(settings: Settings) {
        viewModelScope.launch(Dispatchers.IO) {
            settingsManager.saveSettings(settings)
        }
    }

    private fun getNoteToday() : Note {
        val start = Calendar.getInstance()
        start.set(Calendar.HOUR_OF_DAY, 0)
        start.set(Calendar.MINUTE, 0)
        return noteDao.getNoteFromDate(start.timeInMillis)
    }

    init {
        mutableState.value = mutableState.value.copy(
            settings = Settings(firstLaunch = false)
        )
    }

    companion object {
        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
                val application = checkNotNull(extras[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY])
                val settingsManager = SettingsManager(application.applicationContext)
                val dataSource = NoteDatabase.getInstance(application.applicationContext, CoroutineScope(
                    Dispatchers.IO)
                ).noteDatabaseDao

                return NewNoteViewModel(settingsManager, dataSource) as T
            }
        }
    }
}