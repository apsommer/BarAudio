package com.sommerengineering.baraudio

import android.app.NotificationChannel
import android.app.NotificationChannelGroup
import android.app.NotificationManager
import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.NotificationManagerCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import androidx.navigation.compose.rememberNavController
import com.sommerengineering.baraudio.hilt.readFromDataStore
import com.sommerengineering.baraudio.hilt.token
import com.sommerengineering.baraudio.hilt.writeToDataStore
import com.sommerengineering.baraudio.theme.AppTheme
import dagger.hilt.android.AndroidEntryPoint

var isAppOpen = false
var isUpdateRequired = false
var isOnboardingComplete = false
var areNotificationsEnabled by mutableStateOf(false)

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    val requestNotificationPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()) {
            areNotificationsEnabled = areNotificationsEnabled()
        }

    val updateLauncher =
        registerForActivityResult(
            ActivityResultContracts.StartIntentSenderForResult()) { result ->
            if (result.resultCode != RESULT_OK) {
                logMessage("Update flow failed with code: ${result.resultCode}")
            }
            // system update and restart when update is required/immediate
        }

    // controller to toggle fullscreen
    val windowInsetsController by lazy {
        WindowCompat.getInsetsController(window, window.decorView)
    }

    private fun initNotificationChannel() {

        // create channel
        val channel = NotificationChannel(
            channelId,
            channelName,
            NotificationManager.IMPORTANCE_DEFAULT) // >= DEFAULT to show in status bar

        channel.description = channelDescription
        channel.group = channelGroupId

        // register with system, system takes no action if channel already exists
        val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        manager
            .createNotificationChannelGroup(
                NotificationChannelGroup(
                    channelGroupId,
                    channelGroupName))
        manager.createNotificationChannel(channel)

        areNotificationsEnabled = areNotificationsEnabled()
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        init()

        // launch app
        setContent {

            // toggle full screen
            val isFullScreen = viewModel.isFullScreen
            LaunchedEffect(isFullScreen) {
                applyFullScreen(isFullScreen)
            }

            App(viewModel)
        }
    }

    fun areNotificationsEnabled() : Boolean {

        // get notification channel
        val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        val channel = manager.getNotificationChannel(channelId)

        return manager.areNotificationsEnabled()
            && channel.importance > NotificationManager.IMPORTANCE_NONE
    }

    private fun init() {

        isAppOpen = true

        // load key:values from preferences
        token = readFromDataStore(this, tokenKey) ?: unauthenticatedToken
        isOnboardingComplete = readFromDataStore(this, onboardingKey).toBoolean()

        // dismiss all notifications on launch
        val isLaunchFromNotification = intent.extras?.getBoolean(isLaunchFromNotification) ?: false
        if (isLaunchFromNotification) { cancelAllNotifications(this) }

        // enable layout resizing into system designated screen space
        // can not get behind front camera "notch" of pixel 6a, other apps also can't!
        WindowCompat.setDecorFitsSystemWindows(window, false)

        // init notification channel
        initNotificationChannel()
    }

    private fun applyFullScreen(
        isFullScreen: Boolean) {

        // expand
        if (isFullScreen) {
            windowInsetsController.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            windowInsetsController.hide(WindowInsetsCompat.Type.systemBars())
            return
        }

        // collapse
        windowInsetsController.show(WindowInsetsCompat.Type.systemBars())
    }
}

fun cancelAllNotifications(
    context: Context) =
        NotificationManagerCompat
            .from(context)
            .cancelAll()

@Composable
fun App(
    viewModel: MainViewModel) {

    // track ui mode todo simplify
    viewModel.isSystemInDarkTheme = isSystemInDarkTheme()
    viewModel.setUiMode()

    AppTheme(viewModel.isDarkMode) {
        Scaffold(
            modifier = Modifier.fillMaxSize()) { padding -> padding
            Navigation(
                controller = rememberNavController(),
                viewModel = viewModel
            )
        }
    }
}



