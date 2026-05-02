package com.sommerengineering.signalvoice

import android.app.NotificationChannel
import android.app.NotificationChannelGroup
import android.app.NotificationManager
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.activity.viewModels
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.safeDrawing
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Modifier
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.appupdate.AppUpdateOptions
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability
import com.sommerengineering.signalvoice.navigation.MainNavigation
import com.sommerengineering.signalvoice.theme.AppTheme
import com.sommerengineering.signalvoice.uitls.channelDescription
import com.sommerengineering.signalvoice.uitls.channelGroupId
import com.sommerengineering.signalvoice.uitls.channelGroupName
import com.sommerengineering.signalvoice.uitls.channelId
import com.sommerengineering.signalvoice.uitls.channelName
import com.sommerengineering.signalvoice.uitls.logException
import com.sommerengineering.signalvoice.uitls.logMessage
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import javax.inject.Inject

@AndroidEntryPoint
class MainActivity : ComponentActivity() {

    @Inject
    @ApplicationScope
    lateinit var appScope: CoroutineScope
    private val viewModel: MainViewModel by viewModels()

    val requestNotificationPermissionLauncher =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) {
            viewModel.onNotificationPermissionResult(it)
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
            NotificationManager.IMPORTANCE_DEFAULT
        ) // >= DEFAULT to show in status bar

        channel.description = channelDescription
        channel.group = channelGroupId

        // register with system, system takes no action if channel already exists
        val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        manager
            .createNotificationChannelGroup(
                NotificationChannelGroup(
                    channelGroupId,
                    channelGroupName
                )
            )
        manager.createNotificationChannel(channel)
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        installSplashScreen()
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()

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

    fun areNotificationsEnabled(): Boolean {

        // query system for notification and channel
        val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        val channel = manager.getNotificationChannel(channelId)
        val areNotificationsEnabled = manager.areNotificationsEnabled()
                && channel.importance > NotificationManager.IMPORTANCE_NONE
        return areNotificationsEnabled
    }

    override fun onWindowFocusChanged(hasFocus: Boolean) {
        super.onWindowFocusChanged(hasFocus)
        if (!hasFocus) return
        viewModel.updateNotificationsEnabled(areNotificationsEnabled())
    }
    
    private fun applyFullScreen(
        isFullScreen: Boolean
    ) {

        val controller = WindowCompat.getInsetsController(window, window.decorView)

        if (isFullScreen) {
            controller.systemBarsBehavior =
                WindowInsetsControllerCompat.BEHAVIOR_SHOW_TRANSIENT_BARS_BY_SWIPE
            controller.hide(WindowInsetsCompat.Type.systemBars())
            return
        }

        controller.show(WindowInsetsCompat.Type.systemBars())
    }

    fun checkForcedUpdate() {

        val updateManager = AppUpdateManagerFactory.create(this)

        // request update from play store
        updateManager.appUpdateInfo
            .addOnSuccessListener { updateInfo ->

                // check that update is available, and forced
                if (updateInfo.updateAvailability() != UpdateAvailability.UPDATE_AVAILABLE
                    || 4 >= updateInfo.updatePriority()
                ) {
                    return@addOnSuccessListener
                }

                // todo sign out?

                // launch system update flow ui
                updateManager.startUpdateFlowForResult(
                    updateInfo,
                    updateLauncher,
                    AppUpdateOptions.newBuilder(AppUpdateType.IMMEDIATE).build()
                )
            }

            .addOnFailureListener { exception ->

                // skip exception log for debug build
                if (exception.message?.contains("The app is not owned") == true) return@addOnFailureListener
                logException(exception)
            }
    }
}

@Composable
fun App(
    viewModel: MainViewModel
) {

    // safe drawing area when not in fullscreen
    val insets =
        if (viewModel.isFullScreen) WindowInsets(0)
        else WindowInsets.safeDrawing

    AppTheme {
        Scaffold(contentWindowInsets = insets) { insetsPadding ->
            Box(Modifier.padding(insetsPadding)) {
                MainNavigation(viewModel)
            }
        }
    }
}



