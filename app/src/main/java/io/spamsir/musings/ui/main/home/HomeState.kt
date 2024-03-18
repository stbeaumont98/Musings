package io.spamsir.musings.ui.main.home

import io.spamsir.musings.data.domain.Note

data class HomeState(val noteToday: Note? = null, val recentNotes: List<Note> = listOf(), val rFavNotes: List<Note> = listOf())
