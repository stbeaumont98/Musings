package io.spamsir.musings.events

import io.spamsir.musings.data.Settings

sealed class SettingsEvent {
    data class Save(val settings: Settings) : SettingsEvent()
    data class SetNotification(val time: Long) : SettingsEvent()
}