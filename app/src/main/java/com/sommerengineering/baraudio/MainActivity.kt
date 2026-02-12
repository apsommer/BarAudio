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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.core.app.NotificationManagerCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.appupdate.AppUpdateOptions
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability
import com.sommerengineering.baraudio.theme.AppTheme
import com.sommerengineering.baraudio.theme.isSystemInDarkMode
import dagger.hilt.android.AndroidEntryPoint

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    private val viewModel: MainViewModel by viewModels()

    val requestNotificationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) {
        viewModel.updateNotificationsEnabled(areNotificationsEnabled())
    }

    val updateLauncher =
        registerForActivityResult(ActivityResultContracts.StartIntentSenderForResult()) { result ->
            if (result.resultCode != RESULT_OK) {
                logMessage("Update flow failed with code: ${result.resultCode}")
            }
            // system handles update and restart
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
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

        // initialize dark mode
        viewModel.initDarkMode(isSystemInDarkMode())

        // enable layout resizing into system designated screen space
        WindowCompat.setDecorFitsSystemWindows(window, false)

        initNotificationChannel()
        checkForcedUpdate()

        // launch compose tree
        setContent {

            // toggle full screen
            val isFullScreen = viewModel.isFullScreen
            LaunchedEffect(isFullScreen) { applyFullScreen(isFullScreen) }

            App(viewModel)
        }
    }

    fun areNotificationsEnabled() : Boolean {

        val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        val channel = manager.getNotificationChannel(channelId)

        val areNotificationsEnabled = manager.areNotificationsEnabled()
            && channel.importance > NotificationManager.IMPORTANCE_NONE

        return areNotificationsEnabled
    }

    override fun onResume() {
        super.onResume()
        viewModel.updateNotificationsEnabled(areNotificationsEnabled())
        NotificationManagerCompat.from(this).cancelAll()
    }

    private fun applyFullScreen(
        isFullScreen: Boolean) {

        // controller to toggle fullscreen
        val windowInsetsController = WindowCompat.getInsetsController(window, window.decorView)

        // expand
        if (isFullScreen) {
            windowInsetsController.systemBarsBehavior = WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            windowInsetsController.hide(WindowInsetsCompat.Type.systemBars())
            return
        }

        // collapse
        windowInsetsController.show(WindowInsetsCompat.Type.systemBars())
    }

    fun checkForcedUpdate() {

        val updateManager = AppUpdateManagerFactory.create(this)

        // request update from play store
        updateManager.appUpdateInfo
            .addOnSuccessListener { updateInfo ->

                // check that update is available, and forced
                if (updateInfo.updateAvailability() != UpdateAvailability.UPDATE_AVAILABLE
                    || 4 >= updateInfo.updatePriority()) {
                    return@addOnSuccessListener
                }

                // todo sign out?

                // launch system update flow ui
                updateManager.startUpdateFlowForResult(
                    updateInfo,
                    updateLauncher,
                    AppUpdateOptions.newBuilder(AppUpdateType.IMMEDIATE).build()) }

            .addOnFailureListener { exception ->

                // skip exception log for debug build
                if (exception.message?.contains("The app is not owned") == true) return@addOnFailureListener
                logException(exception)
            }
    }
}

@Composable
fun App(
    viewModel: MainViewModel) {

    val isDarkMode = viewModel.isDarkMode

    AppTheme(isDarkMode) {
        Scaffold(
            modifier = Modifier.fillMaxSize()) { padding -> padding
            Navigation(viewModel)
        }
    }
}



