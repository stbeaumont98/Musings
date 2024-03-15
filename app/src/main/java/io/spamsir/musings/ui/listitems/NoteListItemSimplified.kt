package io.spamsir.musings.ui.listitems

import android.content.Intent
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.layout.wrapContentSize
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Share
import androidx.compose.material3.IconButton
import androidx.compose.material3.Card
import androidx.compose.material3.DropdownMenu
import androidx.compose.material3.DropdownMenuItem
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.spamsir.musings.DateConverter
import io.spamsir.musings.MusingsApplication
import io.spamsir.musings.R
import io.spamsir.musings.data.domain.Note
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.text.SimpleDateFormat
import java.util.Calendar
import java.util.Locale

@Composable
fun NoteListItemSimplified(note: Note, onEvent: (NoteEvent) -> Unit, navEvent: (String) -> Unit) {

    var expanded by remember { mutableStateOf(false) }
    
    val formatter = SimpleDateFormat("MMMM d, yyyy", Locale.US)
    val dateText: String

    val cal = Calendar.getInstance()
    cal.time = DateConverter.toDate(note.dateTime)

    val diff = Calendar.getInstance().timeInMillis - cal.timeInMillis
    dateText = if (diff < ONE_MINUTE) {
        (diff / ONE_SECOND).toString() + " second" + (if ((diff / ONE_SECOND).toInt() != 1) "s" else "") + " ago"
    } else if (diff < ONE_HOUR) {
        (diff / ONE_MINUTE).toString() + " minute" + (if ((diff / ONE_MINUTE).toInt() != 1) "s" else "") + " ago"
    } else if (diff < ONE_DAY) {
        (diff / ONE_HOUR).toString() + " hour" + (if ((diff / ONE_HOUR).toInt() != 1) "s" else "") + " ago"
    } else {
        formatter.format(DateConverter.toDate(note.dateTime))
    }

    Card(
        onClick = {
            navEvent("annotate_screen/" + note.noteId.toString())
        },
        modifier = Modifier
            .padding(6.dp)
            .fillMaxWidth()) {
        Column(modifier = Modifier.padding(10.dp)) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Column {
                    Text(
                        text = dateText,
                        fontSize = 12.sp,
                        modifier = Modifier
                            .padding(horizontal = 8.dp)
                    )
                    Text(
                        text = if (note.title.length > 9) note.title.take(7) + "\u2026" else note.title,
                        fontSize = 24.sp,
                        modifier = Modifier
                            .padding(horizontal = 8.dp)
                    )
                }
                Box(
                    modifier = Modifier
                        .wrapContentSize(Alignment.TopStart)
                ) {
                    IconButton(
                        onClick = { expanded = true }
                    ) {
                        Icon(painterResource(id = R.drawable.ic_more), "More")
                    }
                    DropdownMenu(
                        expanded = expanded,
                        onDismissRequest = { expanded = false },
                        Modifier.width(154.dp)
                    ) {
                        DropdownMenuItem(
                            text = { Text("Favorite") },
                            onClick = {
                                CoroutineScope(Dispatchers.IO).launch {
                                    onEvent(NoteEvent.UpdateNote(note.noteId, !note.isLiked))
                                }
                            },
                            leadingIcon = {
                                Icon(
                                    if (note.isLiked) painterResource(id = R.drawable.ic_btn_star) else painterResource(
                                        id = R.drawable.ic_btn_star_empty
                                    ), "Favorite"
                                )
                            })
                        DropdownMenuItem(
                            text = { Text("Share") },
                            onClick = {
                                CoroutineScope(Dispatchers.IO).launch {
                                    val sendIntent: Intent = Intent().apply {
                                        action = Intent.ACTION_SEND
                                        putExtra(
                                            Intent.EXTRA_TEXT,
                                            note.title + "\n" + note.content
                                        )
                                        type = "text/plain"
                                    }

                                    val shareIntent = Intent.createChooser(sendIntent, null)
                                        .setFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
                                    MusingsApplication.applicationContext()
                                        .startActivity(shareIntent)
                                }
                            },
                            leadingIcon = {
                                Icon(Icons.Filled.Share, "Share")
                            })
                    }
                }
            }
            Text(
                text = note.content,
                modifier = Modifier
                    .padding(start = 8.dp, end = 8.dp, bottom = 8.dp)
            )
        }
    }
}

@Preview
@Composable
fun NoteListItemSimplifiedPreview() {
    val note = Note(Calendar.getInstance().timeInMillis, "Title", "This is the content of this Musing!", false)
    MaterialTheme {
        NoteListItemSimplified(note, {}) {}
    }
}