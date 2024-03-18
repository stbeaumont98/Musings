package io.spamsir.musings.ui.newnote

import android.os.CountDownTimer
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Button
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import io.spamsir.musings.ui.composables.AlertDialog
import io.spamsir.musings.data.domain.Note
import io.spamsir.musings.data.domain.Settings
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Calendar

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun NewNoteScreen(state: NewNoteState, nextTime: Long, onEvent: (NewNoteEvent) -> Unit, navEvent: () -> Unit) {

    val showTodayDialog = remember { mutableStateOf(false) }

    val subtitle = remember { mutableStateOf("2:00") }

    val outOfTime = remember { mutableStateOf(false) }

    val isRunning = remember(false) { mutableStateOf(false) }

    val timer = object : CountDownTimer(nextTime - Calendar.getInstance().timeInMillis, 1000) {

        override fun onTick(p0: Long) {
            isRunning.value = true
            subtitle.value = String.format("%d:%02d", p0 / 60000, (p0 / 1000) % 60)
        }

        override fun onFinish() {
            isRunning.value = false
            outOfTime.value = true
        }

    }

    Scaffold(
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Column(horizontalAlignment = Alignment.CenterHorizontally) {
                        Text("New Musing")
                        Text(subtitle.value, fontSize = 14.sp)
                    }
                }
            )
        }
    ) { innerPadding ->

        LaunchedEffect(Unit) {
            showTodayDialog.value = (state.noteToday != null)
        }

        val title = remember { mutableStateOf("") }
        val content = remember { mutableStateOf("") }

        Surface(modifier = Modifier.fillMaxSize()) {
            Column(modifier = Modifier.padding(innerPadding)) {
                OutlinedTextField(
                    value = title.value,
                    onValueChange = { title.value = it },
                    label = { Text("Title") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, end = 16.dp, top = 16.dp),
                    maxLines = 1
                )
                OutlinedTextField(
                    value = content.value,
                    onValueChange = { content.value = it },
                    label = { Text("What are you thinking?") },
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(300.dp)
                        .padding(start = 16.dp, end = 16.dp, top = 16.dp)
                )
                Row(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(start = 16.dp, end = 16.dp, top = 16.dp),
                    horizontalArrangement = Arrangement.End
                ) {
                    OutlinedButton(
                        onClick = {
                            if (state.settings.firstLaunch) {
                                onEvent(
                                    NewNoteEvent.SaveSettings(
                                        Settings(
                                            state.settings.userName,
                                            state.settings.startTime,
                                            state.settings.endTime,
                                            false
                                        )
                                    )
                                )
                            }
                            timer.cancel()
                            navEvent()
                        },
                        modifier = Modifier.padding(end = 16.dp)
                    ) {
                        Text("Cancel")
                    }
                    Button(
                        enabled = (title.value != "" || content.value != ""),
                        onClick = {
                        CoroutineScope(Dispatchers.IO).launch {
                            val note = Note(
                                Calendar.getInstance().timeInMillis,
                                title.value,
                                content.value,
                                false
                            )
                            onEvent(NewNoteEvent.SaveNote(note))
                        }
                        if (state.settings.firstLaunch) {
                            onEvent(
                                NewNoteEvent.SaveSettings(
                                    Settings(
                                        state.settings.userName,
                                        state.settings.startTime,
                                        state.settings.endTime,
                                        false
                                    )
                                )
                            )
                        }
                        timer.cancel()
                        navEvent()
                    }) {
                        Text("Save")
                    }
                }
            }
        }

        if (state.settings.firstLaunch) {
            val dialogActive = remember { mutableStateOf(true) }
            if (dialogActive.value) {
                AlertDialog(
                    title = "Welcome to Musings!",
                    message = "Next, you'll record your first Musing! You only have 2 minutes, so think fast!",
                    positiveButton = "Continue",
                    onPositiveButton = {
                        dialogActive.value = false
                        timer.start()
                    }
                ) {}
            }
        } else {
            if (!isRunning.value)
                timer.start()
        }

        if (outOfTime.value) {
            timer.cancel()
            AlertDialog(
                title = "Too bad!",
                message = "You ran out of time. Don't worry, there will be other opportunities!",
                positiveButton = "Continue",
                onPositiveButton = {
                    outOfTime.value = false
                    if (state.settings.firstLaunch) {
                        onEvent(
                            NewNoteEvent.SaveSettings(
                                Settings(
                                    state.settings.userName,
                                    state.settings.startTime,
                                    state.settings.endTime,
                                    false
                                )
                            )
                        )
                    }
                    navEvent()
                }) {}
        }

        if (showTodayDialog.value) {
            timer.cancel()
            AlertDialog(
                title = "Too bad!",
                message = "A Musing was already recorded today. Try again tomorrow!",
                positiveButton = "Continue",
                onPositiveButton = {
                    outOfTime.value = false
                    if (state.settings.firstLaunch) {
                        onEvent(
                            NewNoteEvent.SaveSettings(
                                Settings(
                                    state.settings.userName,
                                    state.settings.startTime,
                                    state.settings.endTime,
                                    false
                                )
                            )
                        )
                    }
                    navEvent()
                }) {}
        }
    }
}

@Preview
@Composable
fun NewNoteScreenPreview() {
    MaterialTheme {
        NewNoteScreen(NewNoteState(), 0L, onEvent = {}) {}
    }
}