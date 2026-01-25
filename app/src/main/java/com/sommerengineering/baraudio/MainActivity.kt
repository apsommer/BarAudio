package com.sommerengineering.baraudio

import android.app.NotificationChannel
import android.app.NotificationChannelGroup
import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.NotificationManagerCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.core.view.WindowCompat
import androidx.navigation.compose.rememberNavController
import com.sommerengineering.baraudio.theme.AppTheme
import com.sommerengineering.baraudio.utils.BillingClientImpl
import com.sommerengineering.baraudio.utils.logMessage
import com.sommerengineering.baraudio.utils.readFromDataStore
import com.sommerengineering.baraudio.utils.token
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject
import org.koin.core.parameter.parametersOf

var isAppOpen = false
var isUpdateRequired = false
var isFirstLaunch = true // todo remove this var on resume subscription requirement 310125
lateinit var onboardingProgressRoute: String // todo remove this one too
var areNotificationsEnabled by mutableStateOf(true)

class MainActivity : ComponentActivity() {

    val context = this

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
            // if update is required/immediate (not flexible) play updates and restarts app
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
        val manager = getSystemService(Service.NOTIFICATION_SERVICE) as NotificationManager
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

        init()
        
        // push layout boundary to full screen
        enableEdgeToEdge()
        setContent { App() }
    }

    fun areNotificationsEnabled() : Boolean {

        // get notification channel
        val manager = context.getSystemService(NOTIFICATION_SERVICE) as NotificationManager
        val channel = manager.getNotificationChannel(channelId)

        return manager.areNotificationsEnabled()
            && channel.importance > NotificationManager.IMPORTANCE_NONE
    }

    private fun init() {

        isAppOpen = true
        token = readFromDataStore(context, tokenKey) ?: unauthenticatedToken
        isFirstLaunch = readFromDataStore(context, isFirstLaunchKey)?.toBooleanStrictOrNull() ?: true // todo remove this var on resume subscription requirement 310125
        onboardingProgressRoute = readFromDataStore(context, onboardingKey) ?: OnboardingTextToSpeechScreenRoute

        // dismiss all notifications on launch
        val isLaunchFromNotification = intent.extras?.getBoolean(isLaunchFromNotification) ?: false
        if (isLaunchFromNotification) { cancelAllNotifications(context) }

        // enable layout resizing into system designated screen space
        // can not get behind front camera "notch" of pixel 6a, other apps also can't!
        WindowCompat.setDecorFitsSystemWindows(window, false)

        // init notification channel
        initNotificationChannel()
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

    // toggle show quote
    val showQuote = readFromDataStore(context, showQuoteKey)?.toBooleanStrictOrNull() ?: true
    viewModel.showQuote(context, showQuote)

    // futures webhooks
    val isFuturesWebhooksKey = readFromDataStore(context, isFuturesWebhooksKey)?.toBooleanStrictOrNull() ?: true
    viewModel.setFuturesWebhooks(context, isFuturesWebhooksKey)

    // initialize billing client
    viewModel.initBilling(
        koinInject<BillingClientImpl> { parametersOf(context) })

    AppTheme(viewModel.isDarkMode) {
        Scaffold(
            modifier = Modifier.fillMaxSize()) { padding ->
            Navigation(rememberNavController())
        }
    }
}

