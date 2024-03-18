package io.spamsir.musings.ui.main

import androidx.compose.foundation.focusable
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
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
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.vector.ImageVector
import androidx.compose.ui.platform.LocalFocusManager
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavGraph.Companion.findStartDestination
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import io.spamsir.musings.data.domain.Event
import io.spamsir.musings.ui.listitems.NoteListItem
import io.spamsir.musings.ui.main.allnotes.AllNotesScreen
import io.spamsir.musings.ui.main.favorites.FavoritesScreen
import io.spamsir.musings.ui.main.home.HomeScreen
import io.spamsir.musings.ui.main.allnotes.AllNotesViewModel
import io.spamsir.musings.ui.main.favorites.FavoritesViewModel
import io.spamsir.musings.ui.main.home.HomeViewModel

data class BottomNavigationItem(
    val title: String,
    val selectedIcon: ImageVector,
    val unselectedIcon: ImageVector
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MainScreen(state: MainState, onEvent: (Event) -> Unit, navEvent: (String) -> Unit) {

    val homeViewModel: HomeViewModel = viewModel(factory = HomeViewModel.Factory)
    val allNotesViewModel: AllNotesViewModel = viewModel(factory = AllNotesViewModel.Factory)
    val favoritesViewModel: FavoritesViewModel = viewModel(factory = FavoritesViewModel.Factory)

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
                query = state.searchQuery,
                onQueryChange = { onEvent(MainEvent.OnQueryChange(it)) },
                onSearch = { onEvent(MainEvent.OnQueryChange(it)) },
                active = state.isSearching,
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
                tonalElevation = if (state.isSearching) 0.dp else 6.dp,
                trailingIcon = {
                    if (!state.isSearching) {
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
                LazyColumn {
                    items(state.notesList) { note ->
                        NoteListItem(note, {
                            onEvent(it)
                            homeViewModel.onEvent(it)
                            allNotesViewModel.onEvent(it)
                            favoritesViewModel.onEvent(it)
                        }, navEvent)
                    }
                }
            }

            LaunchedEffect(Unit) {
                focusManager.clearFocus()
            }
        },
        bottomBar =  {
            if (!state.isSearching) {
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
                LaunchedEffect(Unit) {
                    homeViewModel.loadData()
                }
                val homeState = homeViewModel.state.collectAsState()
                HomeScreen(homeState.value, homeViewModel::onEvent, navEvent)
            }
            composable("All Musings") {
                LaunchedEffect(Unit) {
                    allNotesViewModel.loadData()
                }
                val allNotesState = allNotesViewModel.state.collectAsState()
                AllNotesScreen(allNotesState.value, allNotesViewModel::onEvent, navEvent)
            }
            composable("Favorites") {
                LaunchedEffect(Unit) {
                    favoritesViewModel.loadData()
                }
                val favoritesState = favoritesViewModel.state.collectAsState()
                FavoritesScreen(favoritesState.value, favoritesViewModel::onEvent, navEvent)
            }
        }
    }
}

//@Preview
//@Composable
//fun MainScreenPreview() {
//    MaterialTheme {
//        MainScreen(state = MainState(), onEvent = {}) {
//
//        }
//    }
//}