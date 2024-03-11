package io.spamsir.musings.data

data class Search(var searchQuery: String, var isSearching: Boolean, val notesList: List<Note>)