package io.spamsir.musings.ui.settings

import android.app.AlarmManager
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import androidx.core.app.AlarmManagerCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.ViewModelProvider.AndroidViewModelFactory.Companion.APPLICATION_KEY
import androidx.lifecycle.viewModelScope
import androidx.lifecycle.viewmodel.CreationExtras
import dagger.hilt.android.lifecycle.HiltViewModel
import io.spamsir.musings.MusingsApplication
import io.spamsir.musings.data.database.SettingsManager
import io.spamsir.musings.notifications.NotificationsReceiver
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch
import javax.inject.Inject

@HiltViewModel
class SettingsViewModel @Inject constructor(
    private val settingsManager: SettingsManager
) : ViewModel() {

    private val mutableState = MutableStateFlow(SettingsState())
    val state = mutableState.asStateFlow()

    fun loadSettings() {
        viewModelScope.launch {
            mutableState.value = mutableState.value.copy(
                settings = settingsManager.getSettings().stateIn(viewModelScope).value
            )
        }
    }

    fun onEvent(event: SettingsEvent) {
        viewModelScope.launch {
            when (event) {
                is SettingsEvent.Save -> {
                    settingsManager.saveSettings(event.settings)
                    mutableState.value = mutableState.value.copy(
                        settings = event.settings
                    )
                }

                is SettingsEvent.SetNotification -> {
                    setNotification(event.time)
                }
            }
            loadSettings()
        }
    }

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

    private fun setNotification(triggerTime: Long) {
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
                val application = checkNotNull(extras[APPLICATION_KEY])
                val settingsManager = SettingsManager(application.applicationContext)

                return SettingsViewModel(settingsManager) as T
            }
        }
    }
}