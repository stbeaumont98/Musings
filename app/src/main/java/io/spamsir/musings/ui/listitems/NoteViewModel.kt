package io.spamsir.musings.ui.listitems

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.AlarmManagerCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import dagger.hilt.android.lifecycle.HiltViewModel
import io.spamsir.musings.MusingsApplication
import io.spamsir.musings.data.database.NoteDatabase
import io.spamsir.musings.data.database.NoteDatabaseDao
import io.spamsir.musings.notifications.NotificationsReceiver
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class NoteViewModel @Inject constructor(
    private val noteDao: NoteDatabaseDao
) : ViewModel() {

    private val mutableState = MutableStateFlow(NoteState())
    val state = mutableState.asStateFlow()

    private val REQUEST_CODE = 0

    private val notifyPendingIntent: PendingIntent

    private val alarmManager = MusingsApplication.applicationContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager
    private val notifyIntent = Intent(MusingsApplication.applicationContext(), NotificationsReceiver::class.java)

    fun loadData(key: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            mutableState.value = mutableState.value.copy(
                noteId = key,
                dateTime = noteDao.get(key).dateTime,
                title = noteDao.get(key).title,
                content = noteDao.get(key).content,
                isLiked = noteDao.get(key).isLiked
            )
        }
    }

    fun onEvent(event: NoteEvent) {
        when(event) {
            is NoteEvent.UpdateNote -> { noteDao.update(event.key, event.isLiked) }
        }
        loadData(state.value.noteId)
    }

    init {
        notifyPendingIntent = PendingIntent.getBroadcast(
            MusingsApplication.applicationContext(),
            REQUEST_CODE,
            notifyIntent,
            PendingIntent.FLAG_UPDATE_CURRENT or PendingIntent.FLAG_IMMUTABLE
        )
    }

    fun setNotification(triggerTime: Long) {
        AlarmManagerCompat.setExactAndAllowWhileIdle(
            alarmManager,
            AlarmManager.RTC_WAKEUP,
            triggerTime,
            notifyPendingIntent
        )
    }

    companion object {
        val Factory: ViewModelProvider.Factory = object : ViewModelProvider.Factory {
            @Suppress("UNCHECKED_CAST")
            override fun <T : ViewModel> create(modelClass: Class<T>, extras: CreationExtras): T {
                val application = checkNotNull(extras[ViewModelProvider.AndroidViewModelFactory.APPLICATION_KEY])
                val dataSource = NoteDatabase.getInstance(application.applicationContext, CoroutineScope(
                    Dispatchers.IO)
                ).noteDatabaseDao

                return NoteViewModel(dataSource) as T
            }
        }
    }
}