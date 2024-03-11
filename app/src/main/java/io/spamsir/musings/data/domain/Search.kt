package io.spamsir.musings.data.domain

data class Search(var searchQuery: String, var isSearching: Boolean, val notesList: List<Note>)