package io.spamsir.musings.ui

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.core.view.WindowCompat
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import dagger.hilt.android.AndroidEntryPoint
import io.spamsir.musings.R
import io.spamsir.musings.ui.comments.CommentScreen
import io.spamsir.musings.ui.main.MainScreen
import io.spamsir.musings.ui.newnote.NewNoteScreen
import io.spamsir.musings.ui.settings.SettingsScreen
import io.spamsir.musings.ui.theme.MusingsTheme
import io.spamsir.musings.ui.comments.CommentsViewModel
import io.spamsir.musings.ui.main.MainViewModel
import io.spamsir.musings.ui.newnote.NewNoteViewModel
import io.spamsir.musings.ui.settings.SettingsViewModel
import java.util.Calendar

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val settingsViewModel: SettingsViewModel by viewModels(factoryProducer = { SettingsViewModel.Factory })
    private val mainViewModel: MainViewModel by viewModels(factoryProducer = { MainViewModel.Factory })
    private val newNoteViewModel: NewNoteViewModel by viewModels(factoryProducer = { NewNoteViewModel.Factory })
    private val commentsViewModel: CommentsViewModel by viewModels(factoryProducer = { CommentsViewModel.Factory })

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val nextTime = intent.getLongExtra(
            "next_time",
            Calendar.getInstance().timeInMillis + 120000
        )

        settingsViewModel.loadSettings()

        val fromNotification = intent.getBooleanExtra("from_notification", false)

        WindowCompat.setDecorFitsSystemWindows(window, false)

        enableEdgeToEdge()
        setContent {
            MusingsTheme {
                val navController = rememberNavController()

                val settingsState = settingsViewModel.state.collectAsState()

                val startDestination =
                    if (fromNotification) "new_note" else if (settingsState.value.settings.firstLaunch) "settings" else "main"

                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background)
                ) {
                    NavHost(navController, startDestination = startDestination) {
                        composable("main") {
                            LaunchedEffect(Unit) {
                                mainViewModel.loadData()
                            }
                            val state = mainViewModel.state.collectAsState()
                            MainScreen(
                                state.value,
                                mainViewModel::onEvent
                            ) { dest -> navController.navigate(dest) }
                        }
                        composable("settings") {
                            LaunchedEffect(Unit) {
                                settingsViewModel.loadSettings()
                            }
                            val state = settingsViewModel.state.collectAsState()
                            SettingsScreen(state.value, settingsViewModel::onEvent) {
                                dest -> navController.navigate(dest) {
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        }
                        composable("new_note") {
                            LaunchedEffect(Unit) {
                                newNoteViewModel.loadData()
                            }
                            val state = newNoteViewModel.state.collectAsState()
                            NewNoteScreen(state.value, nextTime, newNoteViewModel::onEvent) {
                                navController.navigate("main") {
                                    popUpTo("new_note") { inclusive = true }
                                }
                            }
                        }
                        composable(
                            route = "comment_screen/{noteId}",
                            arguments = listOf(
                                navArgument("noteId") {
                                    defaultValue = 0L
                                    type = NavType.LongType
                                }
                            )
                        ) { navBackStackEntry ->
                            val noteId = navBackStackEntry.arguments?.getLong("noteId")
                            noteId?.let {
                                LaunchedEffect(Unit) {
                                    commentsViewModel.loadData(it)
                                }
                                val state = commentsViewModel.state.collectAsState()
                                CommentScreen(state.value, commentsViewModel::onEvent) {
                                    dest -> navController.popBackStack(dest, false)
                                }
                            }
                        }
                    }
                }
            }
        }

        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            createNotificationChannel()
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun createNotificationChannel() {
        val importance = NotificationManager.IMPORTANCE_HIGH
        val channel = NotificationChannel(
            getString(R.string.notification_channel_id),
            "Musings Notifications",
            importance
        ).apply {
            description = "Notification for Musings"
        }
        val notificationManager =
            this.getSystemService(Context.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)
    }

    companion object {
        @RequiresApi(Build.VERSION_CODES.TIRAMISU)
        val REQUIRED_PERMISSIONS =
            mutableListOf (
                android.Manifest.permission.POST_NOTIFICATIONS,
                android.Manifest.permission.RECEIVE_BOOT_COMPLETED
            )
    }

}
