package io.spamsir.musings.ui.main.states

data class MainState(val searchState: SearchState = SearchState(), val homeState: HomeState = HomeState(), val allNotesState: AllNotesState = AllNotesState(), val favoritesState: FavoritesState = FavoritesState())
