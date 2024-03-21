package io.spamsir.musings.ui.main.states

import io.spamsir.musings.data.domain.Note

data class FavoritesState(val favNotes: List<Note> = listOf())
