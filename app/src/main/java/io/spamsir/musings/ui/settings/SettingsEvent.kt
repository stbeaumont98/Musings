package io.spamsir.musings.ui.settings

import io.spamsir.musings.data.domain.Settings

sealed class SettingsEvent {
    data class Save(val settings: Settings) : SettingsEvent()
    data class SetNotification(val time: Long) : SettingsEvent()
}