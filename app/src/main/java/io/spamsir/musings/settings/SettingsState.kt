package io.spamsir.musings.settings

import io.spamsir.musings.data.Settings

data class SettingsState(val settings: Settings = Settings(firstLaunch = false))
