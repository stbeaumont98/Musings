package io.spamsir.musings.allnotes

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.livedata.observeAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavController
import androidx.navigation.compose.rememberNavController
import io.spamsir.musings.NoteListItem
import io.spamsir.musings.data.Note
import io.spamsir.musings.ui.theme.MusingsTheme
import io.spamsir.musings.viewmodels.NoteViewModel

@Composable
fun AllNotesScreen(state: AllNotesState, navEvent: (String) -> Unit) {

    Surface(
        Modifier.fillMaxSize()
    ) {
        if (state.allNotes.isEmpty()) {
            Column(
                modifier = Modifier.fillMaxSize(),
                verticalArrangement = Arrangement.SpaceEvenly,
                horizontalAlignment = Alignment.CenterHorizontally
            ) {
                Text(text = "No Musings yet!")
            }
        } else {
            LazyColumn(modifier = Modifier.fillMaxSize()) {
                itemsIndexed(state.allNotes) { _, item ->
                    NoteListItem(item, navEvent)
                }
            }
        }
    }
}

@Preview
@Composable
fun AllNotesScreenContentPreview() {
    MusingsTheme {
        AllNotesScreen(
            AllNotesState(
                allNotes = listOf()
            )
        ) {}
    }
}