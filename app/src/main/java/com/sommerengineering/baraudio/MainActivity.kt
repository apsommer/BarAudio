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
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import androidx.navigation.compose.rememberNavController
import com.sommerengineering.baraudio.theme.AppTheme
import kotlinx.coroutines.flow.MutableStateFlow
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.KoinContext
import org.koin.compose.koinInject
import org.koin.core.parameter.parametersOf

var isAppOpen = false
var isUpdateRequired = false
var isFirstLaunch = true // todo remove this var on resume subscription requirement 310125
lateinit var onboardingProgressRoute: String
var isNotificationPermissionGranted = MutableStateFlow(false)

class MainActivity : ComponentActivity() {

    val context = this

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()) { isGranted ->
            if (isGranted) {
                isNotificationPermissionGranted.value = true
                initNotificationChannel()
            }
        }

    val updateLauncher =
        registerForActivityResult(
            ActivityResultContracts.StartIntentSenderForResult()) { result ->
            if (result.resultCode != RESULT_OK) {
                logMessage("Update flow failed with code: ${result.resultCode}")
                return@registerForActivityResult
            }
            // if update is required/immediate (not flexible) play updates and restarts app
        }

    // controller to toggle fullscreen
    val windowInsetsController by lazy { WindowCompat.getInsetsController(window, window.decorView) }

    @SuppressLint("InlinedApi")
    fun requestNotificationPermission() {

        if (isNotificationPermissionGranted.value) return

        // launch system permission request ui
        requestPermissionLauncher
            .launch(Manifest.permission.POST_NOTIFICATIONS)
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

    override fun onCreate(savedInstanceState: Bundle?) {

        installSplashScreen()
        super.onCreate(savedInstanceState)

        init()
        
        // push layout boundary to full screen
        enableEdgeToEdge()
        setContent { App() }
    }

    private fun init() {

        isAppOpen = true
        token = readFromDataStore(context, tokenKey) ?: unauthenticatedToken
        isFirstLaunch = readFromDataStore(context, isFirstLaunchKey)?.toBooleanStrictOrNull() ?: true // todo remove this var on resume subscription requirement 310125
        onboardingProgressRoute = readFromDataStore(context, onboardingKey) ?: OnboardingTextToSpeechScreenRoute

        // check notification permission
        isNotificationPermissionGranted.value =
            32 >= Build.VERSION.SDK_INT || ContextCompat.checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED

        // dismiss all notifications on launch
        val isLaunchFromNotification = intent.extras?.getBoolean(isLaunchFromNotification) ?: false
        if (isLaunchFromNotification) { cancelAllNotifications(context) }

        // enable layout resizing into system designated screen space
        // can not get behind front camera "notch" of pixel 6a, other apps also can't!
        WindowCompat.setDecorFitsSystemWindows(window, false)
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

    // toggle fullscreen
    val isFullScreen = readFromDataStore(context, isFullScreenKey)?.toBoolean() == true
    viewModel.setFullScreen(context, isFullScreen)

    // futures webhooks
    val isFuturesWebhooksKey = readFromDataStore(context, isFuturesWebhooksKey)?.toBooleanStrictOrNull() ?: true
    viewModel.setFuturesWebhooks(context, isFuturesWebhooksKey)

    // initialize billing client
    viewModel.initBilling(
        koinInject<BillingClientImpl> { parametersOf(context) })

    KoinContext {
        AppTheme(viewModel.isDarkMode) {
            Scaffold(
                modifier = Modifier.fillMaxSize()) { padding ->
                Navigation(rememberNavController())
            }
        }
    }
}
