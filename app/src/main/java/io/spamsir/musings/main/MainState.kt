package io.spamsir.musings.main

import io.spamsir.musings.data.Note

data class MainState(val allNotes: List<Note> = listOf(), val searchQuery: String = "", val isSearching: Boolean = false, val notesList: List<Note> = listOf())
