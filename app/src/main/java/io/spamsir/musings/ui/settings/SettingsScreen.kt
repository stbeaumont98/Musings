package io.spamsir.musings.ui.settings

import android.app.AlarmManager
import android.app.NotificationManager
import android.content.Context
import android.content.Intent
import android.os.Build
import android.text.format.DateFormat
import android.widget.Toast
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.interaction.PressInteraction
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.rounded.ArrowBack
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TextButton
import androidx.compose.material3.TimePickerState
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.saveable.rememberSaveable
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import com.google.accompanist.permissions.ExperimentalPermissionsApi
import com.google.accompanist.permissions.isGranted
import com.google.accompanist.permissions.rememberPermissionState
import io.spamsir.musings.ui.MainActivity
import io.spamsir.musings.MusingsApplication
import io.spamsir.musings.R
import io.spamsir.musings.ui.composables.AlertDialog
import io.spamsir.musings.ui.composables.TimePickerDialog
import io.spamsir.musings.data.domain.Settings
import io.spamsir.musings.notifications.cancelNotifications
import java.util.Calendar
import java.util.Locale
import kotlin.random.Random

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(state: SettingsState, onEvent: (SettingsEvent) -> Unit, navEvent: (String) -> Unit) {
    Scaffold (
        topBar = {
            CenterAlignedTopAppBar(
                title = {
                    Text("Settings")
                },
                navigationIcon = {
                    if (!state.settings.firstLaunch) {
                        IconButton(onClick = {
                            navEvent("main")
                        }) {
                            Icon(
                                imageVector = Icons.AutoMirrored.Rounded.ArrowBack,
                                contentDescription = "Return to main screen"
                            )
                        }
                    }
                },
                actions = {
                    TextButton(onClick = {
                        onEvent(
                            SettingsEvent.Save(
                                Settings(
                                    state.settings.userName,
                                    state.settings.startTime,
                                    state.settings.endTime,
                                    state.settings.firstLaunch
                                )
                            )
                        )

                        val notificationManager = ContextCompat.getSystemService(
                            MusingsApplication.applicationContext(),
                            NotificationManager::class.java
                        ) as NotificationManager

                        val alarmMgr = MusingsApplication.applicationContext().getSystemService(
                            Context.ALARM_SERVICE
                        ) as AlarmManager

                        notificationManager.cancelNotifications()

                        if (if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.S) {
                                alarmMgr.canScheduleExactAlarms()
                            } else {
                                true
                            }
                        ) {
                            val startCal = Calendar.getInstance()
                            startCal.add(Calendar.DATE, 1)
                            startCal.set(Calendar.HOUR_OF_DAY, state.settings.startTime.hour)
                            startCal.set(Calendar.MINUTE, state.settings.startTime.minute)

                            val endCal = Calendar.getInstance()
                            endCal.add(Calendar.DATE, 1)
                            endCal.set(Calendar.HOUR_OF_DAY, state.settings.endTime.hour)
                            endCal.set(Calendar.MINUTE, state.settings.endTime.minute)

                            onEvent(
                                SettingsEvent.SetNotification(
                                    if (startCal.timeInMillis < endCal.timeInMillis) Random.nextLong(
                                        startCal.timeInMillis,
                                        endCal.timeInMillis
                                    ) else startCal.timeInMillis
                                )
                            )
                        }

                        if (state.settings.firstLaunch) {
                            navEvent("new_note")
                        } else {
                            navEvent("main")
                        }


                    }) {
                        Text("SAVE")
                    }
                }
            )
        }
    ) { innerPadding ->

        var startTimeText = String.format(
            Locale.US,
            "%d:%02d %s",
            if (state.settings.startTime.hour == 0) 12 else if (state.settings.startTime.hour > 12) state.settings.startTime.hour % 12 else state.settings.startTime.hour,
            state.settings.startTime.minute,
            if (state.settings.startTime.hour >= 12) "PM" else "AM"
        )

        var endTimeText = String.format(
            Locale.US,
            "%d:%02d %s",
            if (state.settings.endTime.hour == 0) 12 else if (state.settings.endTime.hour > 12) state.settings.endTime.hour % 12 else state.settings.endTime.hour,
            state.settings.endTime.minute,
            if (state.settings.endTime.hour >= 12) "PM" else "AM"
        )

        val startTimeSource = remember { MutableInteractionSource() }

        val endTimeSource = remember { MutableInteractionSource() }

        Surface(
            Modifier.fillMaxSize()
        ) {
            Column(modifier = Modifier.padding(innerPadding)) {
//                OutlinedTextField(
//                    value = state.settings.userName,
//                    onValueChange = { state.settings.userName = it },
//                    label = { Text("User name (optional)") },
//                    leadingIcon = {
//                        Icon(Icons.Rounded.Person, contentDescription = "User icon")
//                    },
//                    modifier = Modifier
//                        .fillMaxWidth()
//                        .padding(10.dp)
//                )
                Row(modifier = Modifier.padding(10.dp)) {
                    Icon(
                        painter = painterResource(id = R.drawable.ic_time),
                        contentDescription = "Clock icon"
                    )
                    Text(text = "Active Hours", modifier = Modifier.padding(start = 10.dp))
                }
                Row(
                    modifier = Modifier
                        .fillMaxSize(),
                    horizontalArrangement = Arrangement.SpaceBetween) {
                    OutlinedTextField(
                        value = startTimeText,
                        onValueChange = { startTimeText = it },
                        label = { Text("Start Time") },
                        modifier = Modifier
                            .padding(horizontal = 10.dp)
                            .weight(1f),
                        readOnly = true,
                        interactionSource = startTimeSource
                    )
                    OutlinedTextField(
                        value = endTimeText,
                        onValueChange = { endTimeText = it },
                        label = { Text("End Time") },
                        modifier = Modifier
                            .padding(horizontal = 10.dp)
                            .weight(1f),
                        readOnly = true,
                        interactionSource = endTimeSource
                    )
                }
            }
        }

        val showStartTimePicker = remember { mutableStateOf(false) }
        val showEndTimePicker = remember { mutableStateOf(false) }
        val timePickerState = remember { mutableStateOf(TimePickerState(0, 0, false)) }

        when {
            showStartTimePicker.value -> {
                TimePickerDialog(
                    title = "Select Start Time",
                    onCancel = { showStartTimePicker.value = false },
                    onConfirm = {
                        if (timePickerState.value.hour > state.settings.endTime.hour) {
                            state.settings.startTime.hour = state.settings.endTime.hour
                            state.settings.startTime.minute = state.settings.endTime.minute
                        } else if (timePickerState.value.hour == state.settings.endTime.hour) {
                            state.settings.startTime.hour = timePickerState.value.hour
                            if (timePickerState.value.minute > state.settings.endTime.minute)
                                state.settings.startTime.minute = state.settings.endTime.minute
                            else
                                state.settings.startTime.minute = timePickerState.value.minute
                        } else {
                            state.settings.startTime.hour = timePickerState.value.hour
                            state.settings.startTime.minute = timePickerState.value.minute
                        }
                        showStartTimePicker.value = false
                    },
                    state = timePickerState.value
                )
            }
            showEndTimePicker.value -> {
                TimePickerDialog(
                    title = "Select End Time",
                    onCancel = { showEndTimePicker.value = false },
                    onConfirm = {
                        if (timePickerState.value.hour < state.settings.startTime.hour) {
                            state.settings.endTime.hour = state.settings.startTime.hour
                            state.settings.endTime.minute = state.settings.startTime.minute
                        } else if (timePickerState.value.hour == state.settings.startTime.hour) {
                            state.settings.endTime.hour = timePickerState.value.hour
                            if (timePickerState.value.minute < state.settings.startTime.minute)
                                state.settings.endTime.minute = state.settings.startTime.minute
                            else
                                state.settings.endTime.minute = timePickerState.value.minute
                        } else {
                            state.settings.endTime.hour = timePickerState.value.hour
                            state.settings.endTime.minute = timePickerState.value.minute
                        }
                        showEndTimePicker.value = false
                    },
                    state = timePickerState.value
                )
            }
        }

        val startTimePressedState = startTimeSource.interactions.collectAsState(
            initial = PressInteraction.Cancel(PressInteraction.Press(Offset.Zero))
        )
        if (startTimePressedState.value is PressInteraction.Release) {
            timePickerState.value = TimePickerState(state.settings.startTime.hour, state.settings.startTime.minute, DateFormat.is24HourFormat(
                MusingsApplication.applicationContext()
            ))
            showStartTimePicker.value = true
            startTimeSource.tryEmit(PressInteraction.Cancel(PressInteraction.Press(Offset.Zero)))
        }

        val endTimePressedState = endTimeSource.interactions.collectAsState(
            initial = PressInteraction.Cancel(PressInteraction.Press(Offset.Zero))
        )
        if (endTimePressedState.value is PressInteraction.Release) {
            timePickerState.value = TimePickerState(state.settings.endTime.hour, state.settings.endTime.minute, DateFormat.is24HourFormat(
                MusingsApplication.applicationContext()
            ))
            showEndTimePicker.value = true
            endTimeSource.tryEmit(PressInteraction.Cancel(PressInteraction.Press(Offset.Zero)))
        }

        if (state.settings.firstLaunch) {
            val dialogActive = remember { mutableStateOf(true) }
            if (dialogActive.value) {
                AlertDialog(
                    title = "Welcome to Musings!",
                    message = "It looks like this is your first time opening the app, so lets set some things up!",
                    positiveButton = "Continue",
                    onPositiveButton = {
                        dialogActive.value = false
                    },
                    negativeButton = "",
                    onNegativeButton = {}
                ) {}
            } else {
                CheckPermissions()
            }
        }
    }
}

@OptIn(ExperimentalPermissionsApi::class)
@Composable
fun CheckPermissions() {

    val dismissed = remember { mutableStateOf(false) }

    val context = LocalContext.current

    var doNotShowRationale by rememberSaveable { mutableStateOf(false) }

    if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.TIRAMISU) {
        val alarmPermissionState = rememberPermissionState(android.Manifest.permission.SCHEDULE_EXACT_ALARM)
        if (!alarmPermissionState.status.isGranted && !dismissed.value) {
            AlertDialog(
                title = "Alarm Permission",
                message = "You must allow Musings the ability to set alarms for the app to function properly.",
                positiveButton = "Ok!",
                onPositiveButton = {
                    dismissed.value = true
                    ContextCompat.startActivity(
                        context,
                        Intent(android.provider.Settings.ACTION_REQUEST_SCHEDULE_EXACT_ALARM),
                        null
                    )
                }
            ) {}
        }
        MainActivity.REQUIRED_PERMISSIONS.forEach {
            val permissionState = rememberPermissionState(permission = it)
            if (!permissionState.status.isGranted) {
                if (doNotShowRationale) {
                    Toast.makeText(
                        MusingsApplication.applicationContext(),
                        "Unable to grant notification permissions.", Toast.LENGTH_LONG).show()
                } else {
                    AlertDialog(
                        title = "Notification Permission",
                        message = "You must allow Musings to send you notifications for the app to function properly.",
                        positiveButton = "Ok!",
                        onPositiveButton = {
                            permissionState.launchPermissionRequest()
                        },
                        negativeButton = "No, thanks",
                        onNegativeButton = {
                            doNotShowRationale = true
                        }
                    ) {}
                }
            }
        }
    }
}

@Preview
@Composable
fun SettingsScreenPreview() {
    MaterialTheme {
        SettingsScreen(SettingsState(
            settings = Settings(firstLaunch = false)
        ), onEvent = {}) {}
    }
}