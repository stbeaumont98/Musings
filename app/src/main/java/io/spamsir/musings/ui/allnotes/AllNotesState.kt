package io.spamsir.musings.ui.allnotes

import io.spamsir.musings.data.domain.Note

data class AllNotesState(val allNotes: List<Note> = listOf())
