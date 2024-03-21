package io.spamsir.musings.ui.main

import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.imePadding
import androidx.compose.foundation.layout.navigationBarsPadding
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.statusBarsPadding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.automirrored.outlined.List
import androidx.compose.material.icons.filled.Close
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.outlined.Home
import androidx.compose.material.icons.outlined.Star
import androidx.compose.material.icons.rounded.Search
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBar
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.SearchBar
import androidx.compose.material3.SearchBarDefaults
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import io.spamsir.musings.data.domain.Event
import io.spamsir.musings.ui.listitems.NoteListItem
import io.spamsir.musings.ui.main.allnotes.AllNotesScreen
import io.spamsir.musings.ui.main.favorites.FavoritesScreen
import io.spamsir.musings.ui.main.home.HomeScreen
import io.spamsir.musings.ui.main.states.MainState

data class BottomNavigationItem(
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(state: MainState, onEvent: (Event) -> Unit, navEvent: (String) -> Unit) {

    val navController = rememberNavController()

    val items = listOf(
        BottomNavigationItem(
            title = "Home",
            selectedIcon = Icons.Filled.Home,
            unselectedIcon = Icons.Outlined.Home
        ),
        BottomNavigationItem(
            title = "All Musings",
            selectedIcon = Icons.AutoMirrored.Filled.List,
            unselectedIcon = Icons.AutoMirrored.Outlined.List
        ),
        BottomNavigationItem(
            title = "Favorites",
            selectedIcon = Icons.Filled.Star,
            unselectedIcon = Icons.Outlined.Star
        )
    )

    var selectedItemIndex by rememberSaveable {
        mutableIntStateOf(0)
    }

    Scaffold (
        topBar = {
            val focusManager = LocalFocusManager.current
            SearchBar(
                query = state.searchState.searchQuery,
                onQueryChange = { onEvent(MainEvent.OnQueryChange(it)) },
                onSearch = { onEvent(MainEvent.OnQueryChange(it)) },
                active = state.searchState.isSearching,
                onActiveChange = {
                    onEvent(MainEvent.OnToggle)
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(
                        PaddingValues(
                            start = 12.dp,
                            top = 2.dp,
                            end = 12.dp,
                            bottom = 12.dp
                        )
                    )
                    .focusable(),
                placeholder = { Text("Search") },
                leadingIcon = {
                    Icon(
                        imageVector = Icons.Rounded.Search,
                        contentDescription = null,
                        tint = MaterialTheme.colorScheme.onSurfaceVariant
                    )
                },
                colors = SearchBarDefaults.colors(),
                tonalElevation = if (state.searchState.isSearching) 0.dp else 6.dp,
                trailingIcon = {
                    if (!state.searchState.isSearching) {
                        IconButton(
                            onClick = { navEvent("settings") }
                        ) {
                            Icon(Icons.Filled.Settings, "Settings")
                        }
                    } else {
                        IconButton(
                            onClick = { onEvent(MainEvent.OnToggle) }) {
                            Icon(Icons.Filled.Close, "Close Search")
                        }
                    }
                }
            ) {
                LazyColumn(
                    modifier = Modifier
                        .statusBarsPadding()
                        .navigationBarsPadding().imePadding()
                ) {
                    items(state.searchState.notesList) { note ->
                        NoteListItem(note, {
                            onEvent(it)
                        }, navEvent)
                    }
                }
            }

            LaunchedEffect(Unit) {
                focusManager.clearFocus()
            }
        },
        bottomBar =  {
            if (!state.searchState.isSearching) {
                NavigationBar {
                    items.forEachIndexed { index, item ->
                        NavigationBarItem(
                            icon = {
                                Icon(
                                    imageVector = if (index == selectedItemIndex) item.selectedIcon else item.unselectedIcon,
                                    contentDescription = item.title
                                )
                            },
                            label = { Text(item.title) },
                            selected = selectedItemIndex == index,
                            onClick = {
                                selectedItemIndex = index
                                navController.navigate(item.title) {
                                    popUpTo(navController.graph.findStartDestination().id) {
                                        saveState = true
                                    }
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        )
                    }
                }
            }
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = "Home",
            modifier = Modifier.padding(innerPadding)) {
            composable("Home") {
                HomeScreen(state.homeState, onEvent, navEvent)
            }
            composable("All Musings") {
                AllNotesScreen(state.allNotesState, onEvent, navEvent)
            }
            composable("Favorites") {
                FavoritesScreen(state.favoritesState, onEvent, navEvent)
            }
        }
    }
}

@Preview
@Composable
fun MainScreenPreview() {
    MaterialTheme {
        MainScreen(
            state = MainState(),
            onEvent = {}) {}
    }
}