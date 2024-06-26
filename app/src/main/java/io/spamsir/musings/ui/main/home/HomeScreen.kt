package io.spamsir.musings.ui.main.home

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.wrapContentHeight
import androidx.compose.foundation.lazy.staggeredgrid.LazyVerticalStaggeredGrid
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridCells
import androidx.compose.foundation.lazy.staggeredgrid.StaggeredGridItemSpan
import androidx.compose.foundation.lazy.staggeredgrid.itemsIndexed
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.spamsir.musings.DateConverter
import io.spamsir.musings.data.domain.Note
import io.spamsir.musings.ui.listitems.NoteEvent
import io.spamsir.musings.ui.listitems.NoteListItem
import io.spamsir.musings.ui.listitems.NoteListItemSimplified
import io.spamsir.musings.ui.main.states.HomeState
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@Composable
fun HomeScreen(
    state: HomeState,
    onEvent: (NoteEvent) -> Unit,
    navEvent: (String) -> Unit
) {

    val formatter = SimpleDateFormat("MMMM d, yyyy", Locale.US)

    Surface(
        Modifier.fillMaxSize()
    ) {
        LazyVerticalStaggeredGrid(
            columns = StaggeredGridCells.Fixed(2),
            modifier = Modifier.padding(horizontal = 12.dp)
        ) {
            item(span = StaggeredGridItemSpan.FullLine) {
                Text(
                    text = formatter.format(DateConverter.toDate(Calendar.getInstance().timeInMillis)),
                    fontSize = 32.sp,
                    modifier = Modifier.padding(start = 8.dp, top = 16.dp, bottom = 16.dp)
                )
            }
            if (state.noteToday != null) {
                item(span = StaggeredGridItemSpan.FullLine) {
                    NoteListItem(state.noteToday, onEvent, navEvent)
                }
            } else {
                item(span = StaggeredGridItemSpan.FullLine) {
                    Text(text = "No Musing today!",
                        textAlign = TextAlign.Center,
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(120.dp)
                            .wrapContentHeight())
                }
            }
            if (state.recentNotes.isNotEmpty()) {
                item(span = StaggeredGridItemSpan.FullLine) {
                    Text(
                        text = "Recent",
                        fontSize = 28.sp,
                        modifier = Modifier.padding(start = 8.dp, top = 16.dp, bottom = 16.dp)
                    )
                }
                itemsIndexed(state.recentNotes) { _, item ->
                    NoteListItemSimplified(item, onEvent, navEvent)
                }
            }
            if (state.rFavNotes.isNotEmpty()) {
                item(span = StaggeredGridItemSpan.FullLine) {
                    Text(
                        text = "Favorites",
                        fontSize = 28.sp,
                        modifier = Modifier.padding(start = 8.dp, top = 16.dp, bottom = 16.dp)
                    )
                }
                itemsIndexed(state.rFavNotes) { _, item ->
                    NoteListItemSimplified(item, onEvent, navEvent)
                }
            }
        }
    }
}

@Preview
@Composable
fun HomeScreenContentPreview() {
    MaterialTheme {
        HomeScreen(
            HomeState(
                noteToday = Note(title = "Today's Musing", content = "This is today's Musing!", isLiked = true),
                recentNotes = listOf(
                    Note(title = "Title 1", content = "This is short."),
                    Note(title = "Title 2", content = "This Musing is a little bit longer."),
                    Note(title = "Title 3", content = "This Musing is even longer! I don't know what else to write to get this one longer than the previous."),
                    Note(title = "Title 4", content = "This one can be short again.")
                ),
                rFavNotes = listOf(
                    Note(title = "Title 3", content = "This Musing is even longer! I don't know what else to write to get this one longer than the previous.", isLiked = true),
                    Note(title = "Fav 2", content = "This one is shorter.", isLiked = true),
                    Note(title = "Fav 3", content = "", isLiked = true)
                )
            ),
            {}
        ) {}
    }
}

