package io.spamsir.musings

import android.app.NotificationChannel
import android.app.NotificationManager
import android.content.Context
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.viewModels
import androidx.annotation.RequiresApi
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.Modifier
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import dagger.hilt.android.AndroidEntryPoint
import io.spamsir.musings.annotations.AnnotateScreen
import io.spamsir.musings.data.Settings
import io.spamsir.musings.main.MainScreen
import io.spamsir.musings.newnote.NewNoteScreen
import io.spamsir.musings.settings.SettingsScreen
import io.spamsir.musings.ui.theme.MusingsTheme
import io.spamsir.musings.viewmodels.MainViewModel
import io.spamsir.musings.viewmodels.NewNoteViewModel
import io.spamsir.musings.viewmodels.SettingsViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import java.util.Calendar

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val settingsViewModel: SettingsViewModel by viewModels(factoryProducer = { SettingsViewModel.Factory })
    private val mainViewModel: MainViewModel by viewModels(factoryProducer = { MainViewModel.Factory })
    private val newNoteViewModel: NewNoteViewModel by viewModels(factoryProducer = { NewNoteViewModel.Factory })

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val nextTime = intent.getLongExtra(
            "next_time",
            Calendar.getInstance().timeInMillis + 120000
        )

        CoroutineScope(Dispatchers.IO).launch {
            mainViewModel.loadData()
            settingsViewModel.loadSettings()
            newNoteViewModel.loadData(nextTime)
        }

        val settingsState = settingsViewModel.state

        val fromNotification = intent.getBooleanExtra("from_notification", false)

        val startDestination =
            if (fromNotification) "new_note" else if (settingsState.value.settings.firstLaunch) "settings" else "main"

        setContent {
            MusingsTheme {
                val navController = rememberNavController()

                Surface(
                    modifier = Modifier
                        .fillMaxSize()
                        .background(MaterialTheme.colorScheme.background)
                ) {
                    NavHost(navController, startDestination = startDestination) {
                        composable("main") {
                            val state = mainViewModel.state.collectAsState()
                            MainScreen(state.value, mainViewModel::onEvent) { dest -> navController.navigate(dest) }
                        }
                        composable("settings") {
                            val state = settingsViewModel.state.collectAsState()
                            SettingsScreen(state.value, settingsViewModel::onEvent) {
                                dest -> navController.navigate(dest) {
                                    launchSingleTop = true
                                    restoreState = true
                                }
                            }
                        }
                        composable("new_note") {
                            val state = newNoteViewModel.state.collectAsState()
                            NewNoteScreen(state.value, newNoteViewModel::onEvent) {
                                navController.navigate("main") {
                                    popUpTo("new_note") { inclusive = true }
                                }
                            }
                        }
                        composable(
                            route = "annotate_screen/{noteId}",
                            arguments = listOf(
                                navArgument("noteId") {
                                    defaultValue = 0L
                                    type = NavType.LongType
                                }
                            )
                        ) { navBackStackEntry ->
                            val noteId = navBackStackEntry.arguments?.getLong("noteId")
                            noteId?.let { AnnotateScreen(it, navController) }
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
        private const val REQUEST_CODE_PERMISSIONS = 10
        @RequiresApi(Build.VERSION_CODES.TIRAMISU)
        val REQUIRED_PERMISSIONS =
            mutableListOf (
                android.Manifest.permission.POST_NOTIFICATIONS,
                android.Manifest.permission.RECEIVE_BOOT_COMPLETED
            )
    }

}