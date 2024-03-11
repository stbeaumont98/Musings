package io.spamsir.musings.events

sealed class MainEvent {
    data class OnQueryChange(val text: String) : MainEvent()
    data object OnToggle : MainEvent()
}