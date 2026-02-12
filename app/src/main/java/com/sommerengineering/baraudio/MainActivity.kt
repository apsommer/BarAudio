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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.core.app.NotificationManagerCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import androidx.core.view.WindowInsetsCompat
import androidx.core.view.WindowInsetsControllerCompat
import androidx.credentials.CredentialManager
import androidx.navigation.NavHostController
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.appupdate.AppUpdateOptions
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability
import com.sommerengineering.baraudio.theme.AppTheme
import com.sommerengineering.baraudio.theme.isSystemInDarkMode
import dagger.hilt.android.AndroidEntryPoint

var isAppOpen = false
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

        // launch compose tree
        setContent {

            // toggle full screen
            val isFullScreen = viewModel.isFullScreen
            LaunchedEffect(isFullScreen) { applyFullScreen(isFullScreen) }

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

        // todo collect all in init() method of viewmodel
        viewModel.initOnboarding()
        viewModel.initDarkMode(isSystemInDarkMode())

        // dismiss all notifications on launch
        val isLaunchFromNotification = intent.extras?.getBoolean(isLaunchFromNotification) ?: false
        if (isLaunchFromNotification) { cancelAllNotifications(this) }

        // enable layout resizing into system designated screen space
        // can not get behind front camera "notch" of pixel 6a, other apps also can't!
        WindowCompat.setDecorFitsSystemWindows(window, false)

        // init notification channel
        initNotificationChannel()

        checkForcedUpdate()
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

fun cancelAllNotifications(context: Context) =
    NotificationManagerCompat.from(context).cancelAll()

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



