package io.spamsir.musings.ui.main.states

import io.spamsir.musings.data.domain.Note

data class HomeState(val noteToday: Note? = null, val recentNotes: List<Note> = listOf(), val rFavNotes: List<Note> = listOf())
