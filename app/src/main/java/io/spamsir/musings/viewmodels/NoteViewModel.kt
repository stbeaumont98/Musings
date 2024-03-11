package io.spamsir.musings.viewmodels

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
import io.spamsir.musings.NoteState
import io.spamsir.musings.data.Note
import io.spamsir.musings.database.NoteDatabase
import io.spamsir.musings.database.NoteDatabaseDao
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
    fun updateNote(key: Long, isLiked: Boolean) = noteDao.update(key, isLiked)
    fun getNote(key: Long) : Note = noteDao.get(key)

    private val REQUEST_CODE = 0

    private val notifyPendingIntent: PendingIntent

    private val alarmManager = MusingsApplication.applicationContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager
    private val notifyIntent = Intent(MusingsApplication.applicationContext(), NotificationsReceiver::class.java)

    fun loadData(key: Long) {
        viewModelScope.launch(Dispatchers.IO) {
            mutableState.value = mutableState.value.copy(
                dateTime = noteDao.get(key).dateTime,
                title = noteDao.get(key).title,
                content = noteDao.get(key).content,
                isLiked = noteDao.get(key).isLiked
            )
        }
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

                return AllNotesViewModel(dataSource) as T
            }
        }
    }
}