package io.spamsir.musings.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import dagger.hilt.android.lifecycle.HiltViewModel
import io.spamsir.musings.data.Note
import io.spamsir.musings.database.NoteDatabase
import io.spamsir.musings.database.NoteDatabaseDao
import io.spamsir.musings.home.HomeState
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import java.util.Calendar
import javax.inject.Inject

@HiltViewModel
class HomeViewModel @Inject constructor(
    private val noteDao: NoteDatabaseDao
): ViewModel() {

    private val mutableState = MutableStateFlow(HomeState())
    val state = mutableState.asStateFlow()

    fun loadData() {
        viewModelScope.launch(Dispatchers.IO) {
            mutableState.value = mutableState.value.copy(
                noteToday = getNoteToday(),
                recentNotes = recentNotes(),
                rFavNotes = recentNotes(true)
            )
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

    companion object {
        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
                val application = checkNotNull(extras[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY])
                val dataSource = NoteDatabase.getInstance(application.applicationContext, CoroutineScope(Dispatchers.IO)).noteDatabaseDao

                return HomeViewModel(dataSource) as T
            }
        }
    }
}