package io.spamsir.musings.ui.listitems

import android.content.Intent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.rounded.Share
import androidx.compose.material3.Card
import androidx.compose.material3.FilledIconToggleButton
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.spamsir.musings.DateConverter
import io.spamsir.musings.MusingsApplication
import io.spamsir.musings.R
import io.spamsir.musings.data.domain.Note
import io.spamsir.musings.ui.theme.MusingsTheme
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

const val ONE_SECOND = 1000
const val ONE_MINUTE = 60000
const val ONE_HOUR = 3600000
const val ONE_DAY = 86400000

@Composable
fun NoteListItem(state: NoteState, onEvent: (NoteEvent) -> Unit, navEvent: (String) -> Unit) {
    
    val formatter = SimpleDateFormat("MMMM d, yyyy", Locale.US)
    val dateText: String

    val cal = Calendar.getInstance()
    cal.time = DateConverter.toDate(state.dateTime)

    val diff = Calendar.getInstance().timeInMillis - cal.timeInMillis
    dateText = if (diff < ONE_MINUTE) {
        (diff / ONE_SECOND).toString() + " second" + (if ((diff / ONE_SECOND).toInt() != 1) "s" else "") + " ago"
    } else if (diff < ONE_HOUR) {
        (diff / ONE_MINUTE).toString() + " minute" + (if ((diff / ONE_MINUTE).toInt() != 1) "s" else "") + " ago"
    } else if (diff < ONE_DAY) {
        (diff / ONE_HOUR).toString() + " hour" + (if ((diff / ONE_HOUR).toInt() != 1) "s" else "") + " ago"
    } else {
        formatter.format(DateConverter.toDate(state.dateTime))
    }

    Card(
        onClick = {
            navEvent("annotate_screen/" + state.noteId.toString())
        },
        modifier = Modifier
            .padding(8.dp)
            .fillMaxWidth()) {
        Column(modifier = Modifier.padding(10.dp)) {
            Row(modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween) {
                Column {
                    Text(
                        text = dateText,
                        fontSize = 12.sp,
                        modifier = Modifier
                            .padding(horizontal = 8.dp)
                    )
                    Text(
                        text = if (state.title.length > 20) state.title.take(18) + "\u2026" else state.title,
                        fontSize = 24.sp,
                        modifier = Modifier
                            .padding(horizontal = 8.dp)
                    )
                }
                Row {
                    IconButton(
                        onClick = {
                            CoroutineScope(Dispatchers.IO).launch {
                                val sendIntent: Intent = Intent().apply {
                                    action = Intent.ACTION_SEND
                                    putExtra(Intent.EXTRA_TEXT, state.title + "\n" + state.content)
                                    type = "text/plain"
                                }

                                val shareIntent = Intent.createChooser(sendIntent, null)
                                    .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                MusingsApplication.applicationContext().startActivity(shareIntent)
                            }
                        }
                    ) {
                        Icon(Icons.Rounded.Share, "Share")
                    }
                    FilledIconToggleButton(
                        checked = state.isLiked,
                        onCheckedChange = {
                            CoroutineScope(Dispatchers.IO).launch {
                                onEvent(NoteEvent.UpdateNote(state.noteId, !state.isLiked))
                            }
                        }
                    ) {
                        Icon(
                            if (state.isLiked) painterResource(id = R.drawable.ic_btn_star) else painterResource(
                                id = R.drawable.ic_btn_star_empty
                            ), "Favorite"
                        )
                    }
                }
            }

            Text(
                text = state.content,
                modifier = Modifier
                    .padding(start = 8.dp, end = 8.dp, bottom = 8.dp)
            )
        }
    }
}

@Preview
@Composable
fun NoteListItemPreview() {
    val note = Note(Calendar.getInstance().timeInMillis, "Title", "Content...", false)
    MusingsTheme {
        NoteListItem(NoteState(), {}) {}
    }
}