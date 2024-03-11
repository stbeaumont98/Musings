package io.spamsir.musings.ui.allnotes

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.itemsIndexed
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.lifecycle.viewmodel.compose.viewModel
import io.spamsir.musings.ui.listitems.NoteListItem
import io.spamsir.musings.ui.theme.MusingsTheme
import io.spamsir.musings.ui.listitems.NoteViewModel

@Composable
fun AllNotesScreen(state: AllNotesState, navEvent: (String) -> Unit) {

    val noteViewModel: NoteViewModel = viewModel(factory = NoteViewModel.Factory)

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
                    noteViewModel.loadData(item.noteId)
                    val noteState = noteViewModel.state.collectAsState()
                    NoteListItem(noteState.value, noteViewModel::onEvent, navEvent)
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