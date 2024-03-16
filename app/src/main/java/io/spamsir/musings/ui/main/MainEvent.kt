package io.spamsir.musings.ui.main

import io.spamsir.musings.data.domain.Event

sealed class MainEvent: Event {
    data class OnQueryChange(val text: String) : MainEvent()
    data object OnToggle : MainEvent()
}