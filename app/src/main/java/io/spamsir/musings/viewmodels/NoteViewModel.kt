package io.spamsir.musings.viewmodels

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.AlarmManagerCompat
import androidx.lifecycle.ViewModel
import io.spamsir.musings.MusingsApplication
import io.spamsir.musings.data.Note
import io.spamsir.musings.database.NoteDatabaseDao
import io.spamsir.musings.notifications.NotificationsReceiver

class NoteViewModel(private val noteDao: NoteDatabaseDao) : ViewModel() {



    fun updateNote(key: Long, isLiked: Boolean) = noteDao.update(key, isLiked)
    fun getNote(key: Long) : Note = noteDao.get(key)

    private val REQUEST_CODE = 0

    private val notifyPendingIntent: PendingIntent

    private val alarmManager = MusingsApplication.applicationContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager
    private val notifyIntent = Intent(MusingsApplication.applicationContext(), NotificationsReceiver::class.java)

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
        @Volatile
        private var instance: NoteViewModel? = null

        fun getInstance(noteDao: NoteDatabaseDao) =
            instance ?: synchronized(this) {
                instance ?: NoteViewModel(noteDao).also { instance = it }
            }
    }
}