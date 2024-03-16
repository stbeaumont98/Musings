package io.spamsir.musings.ui.favorites

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import io.spamsir.musings.ui.listitems.NoteEvent
import io.spamsir.musings.ui.listitems.NoteListItem

@Composable
fun FavoritesScreen(state: FavoritesState, onEvent: (NoteEvent) -> Unit, navEvent: (String) -> Unit) {

    Surface(
        Modifier.fillMaxSize()
    ) {
        if (state.favNotes.isEmpty()) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceEvenly,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "No favorites yet!")
            }
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                itemsIndexed(state.favNotes) { _, item ->
                    NoteListItem(item, onEvent, navEvent)
                }
            }
        }
    }
}

//@Preview
//@Composable
//fun FavoritesScreenPreview() {
//    val f = listOf(Note(), Note())
//    MaterialTheme {
//        FavoritesScreen(
//            FavoritesState(
//                favNotes = listOf()
//            )
//        ) {}
//    }
//}