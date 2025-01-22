package com.sommerengineering.baraudio

import android.Manifest
import android.annotation.SuppressLint
import android.app.NotificationChannel
import android.app.NotificationChannelGroup
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.MutableState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.rememberNavController
import com.sommerengineering.baraudio.theme.AppTheme
import org.koin.android.ext.android.get
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.KoinContext
import org.koin.compose.koinInject
import org.koin.core.parameter.parametersOf

var isAppOpen = false
var isUpdateRequired = false
var isOnboardingComplete = false
var isNotificationPermissionGranted by mutableStateOf(false)

class MainActivity : ComponentActivity() {

    val context = this

    override fun onCreate(savedInstanceState: Bundle?) {

        installSplashScreen()
        super.onCreate(savedInstanceState)

        // init
        isAppOpen = true
        get<TextToSpeechImpl>() // todo remove? instantiated in viewmodel creation
        token = readFromDataStore(context, tokenKey) ?: unauthenticatedToken
        isOnboardingComplete = readFromDataStore(context, onboardingKey).toBoolean()
        isNotificationPermissionGranted =
            Build.VERSION.SDK_INT < 33 || // realtime permission required if sdk >= 32
                ContextCompat.checkSelfPermission( // permission already granted
                    context,
                    Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED

        // dismiss notifications after launch
        val isLaunchFromNotification = intent.extras?.getBoolean(isLaunchFromNotification) ?: false
        if (isLaunchFromNotification) { cancelAllNotifications(context) }

        enableEdgeToEdge()
        setContent { App() }
    }

    val updateLauncher =

        registerForActivityResult(
            ActivityResultContracts.StartIntentSenderForResult()) { result ->

            if (result.resultCode != RESULT_OK) {

                logMessage("Update flow failed with code: ${result.resultCode}")
                return@registerForActivityResult
            }

            // since update is immediate (not flexible) play updates and restarts app
        }

    @SuppressLint("InlinedApi")
    fun requestNotificationPermission() {

        if (isNotificationPermissionGranted) return

        // launch system permission request ui
        requestPermissionLauncher
            .launch(Manifest.permission.POST_NOTIFICATIONS)
    }

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()) { isGranted ->
                if (isGranted) {
                    isNotificationPermissionGranted = true
                    initNotificationChannel()
                }
            }

    private fun initNotificationChannel() {

        // create channel
        val channel = NotificationChannel(
            channelId,
            channelName,
            NotificationManager.IMPORTANCE_DEFAULT) // >= DEFAULT to show in status bar

        channel.description = channelDescription
        channel.group = channelGroupId

        // register with system
        val notificationManager =
            getSystemService(Service.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager
            .createNotificationChannelGroup(
                NotificationChannelGroup(
                    channelGroupId,
                    channelGroupName))
        notificationManager
            .createNotificationChannel(channel)

        logMessage("Notification channel registered")
    }
}

fun cancelAllNotifications(
    context: Context) =
        NotificationManagerCompat
            .from(context)
            .cancelAll()

@Composable
fun App() {

    // inject viewmodel
    val context = LocalContext.current
    val viewModel: MainViewModel = koinViewModel(viewModelStoreOwner = context as MainActivity)

    // track ui mode
    viewModel.isSystemInDarkTheme = isSystemInDarkTheme()
    viewModel.setUiMode(context)
    val isDarkMode by remember { viewModel.isDarkMode }

    // initialize billing client
    viewModel.initBilling(
        koinInject<BillingClientImpl> { parametersOf(context) })

    KoinContext {
        AppTheme(isDarkMode) {
            Scaffold { padding ->
                Modifier.padding(padding) // not used
                Navigation(rememberNavController())
            }
        }
    }
}
