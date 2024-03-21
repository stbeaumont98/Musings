package io.spamsir.musings.ui.main.states

import io.spamsir.musings.data.domain.Note

data class AllNotesState(val allNotes: List<Note> = listOf())
