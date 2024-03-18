package io.spamsir.musings.ui.main.favorites

import io.spamsir.musings.data.domain.Note

data class FavoritesState(val favNotes: List<Note> = listOf())
