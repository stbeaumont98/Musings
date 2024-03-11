package io.spamsir.musings.ui.main

sealed class MainEvent {
    data class OnQueryChange(val text: String) : MainEvent()
    data object OnToggle : MainEvent()
}