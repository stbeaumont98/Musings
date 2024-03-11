package io.spamsir.musings.favorites

import io.spamsir.musings.data.Note

data class FavoritesState(val favNotes: List<Note> = listOf())
