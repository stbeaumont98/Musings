package io.spamsir.musings.home

import io.spamsir.musings.data.Note

data class HomeState(val noteToday: Note? = null, val recentNotes: List<Note> = listOf(), val rFavNotes: List<Note> = listOf())
