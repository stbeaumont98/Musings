package io.spamsir.musings.ui.main.states

import io.spamsir.musings.data.domain.Note

data class SearchState(val allNotes: List<Note> = listOf(), val searchQuery: String = "", val isSearching: Boolean = false, val notesList: List<Note> = listOf())
