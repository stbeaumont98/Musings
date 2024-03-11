package io.spamsir.musings.notifications

import android.app.AlarmManager
import android.app.NotificationManager
import android.content.BroadcastReceiver
import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.compose.runtime.collectAsState
import androidx.core.content.ContextCompat
import io.spamsir.musings.MusingsApplication
import io.spamsir.musings.data.Settings
import io.spamsir.musings.data.SettingsManager
import io.spamsir.musings.database.NoteDatabase
import io.spamsir.musings.viewmodels.NoteViewModel
import io.spamsir.musings.viewmodels.SettingsViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Calendar
import kotlin.random.Random

class NotificationsReceiver: BroadcastReceiver() {

    private lateinit var settingsManager: SettingsManager
    private lateinit var settings : Settings

    private var messageList = listOf(
        "Quick!",
        "Think fast!",
        "No hesitation!",
        "Hurry!"
    )

    private var messageList0 = listOf(
        "What's on your mind?",
        "What are you thinking?"
    )

    override fun onReceive(p0: Context, p1: Intent?) {
        val notificationManager = ContextCompat.getSystemService(
            p0,
            NotificationManager::class.java
        ) as NotificationManager

        settingsManager = SettingsManager(MusingsApplication.applicationContext())
        val settingsViewModel = SettingsViewModel(settingsManager)

        CoroutineScope(Dispatchers.IO).launch {
            settingsViewModel.loadSettings()

            settingsViewModel.state.collect {
                settings = it.settings
            }
        }

        val dataSource = NoteDatabase.getInstance(p0, CoroutineScope(Dispatchers.IO)).noteDatabaseDao
        val noteViewModel = NoteViewModel.getInstance(dataSource)

        notificationManager.cancelNotifications()

        notificationManager.sendNotification(
            messageList[Random.nextInt(4)] + " " +messageList0[Random.nextInt(2)],
            p0
        )

        // Schedule the next notification

        val alarmMgr = MusingsApplication.applicationContext().getSystemService(Context.ALARM_SERVICE) as AlarmManager

//        if (if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
//            alarmMgr.canScheduleExactAlarms()
//        } else {
//            true
//        }) {
//            CoroutineScope(Dispatchers.Main).launch {
//                settings.collect {data ->
//
//                    val startCal = Calendar.getInstance()
//                    startCal.add(Calendar.DATE, 1)
//                    startCal.set(Calendar.HOUR_OF_DAY, data.startTime.hour)
//                    startCal.set(Calendar.MINUTE, data.startTime.minute)
//
//                    val endCal = Calendar.getInstance()
//                    endCal.add(Calendar.DATE, 1)
//                    endCal.set(Calendar.HOUR_OF_DAY, data.endTime.hour)
//                    endCal.set(Calendar.MINUTE, data.endTime.minute)
//
//                    noteViewModel.setNotification(
//                        if (startCal.timeInMillis < endCal.timeInMillis) Random.nextLong(
//                            startCal.timeInMillis,
//                            endCal.timeInMillis
//                        ) else startCal.timeInMillis
//                    )
//                }
//            }
//        }
    }
}