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
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import androidx.core.splashscreen.SplashScreen.Companion.installSplashScreen
import androidx.navigation.compose.rememberNavController
import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.ktx.Firebase
import com.sommerengineering.baraudio.theme.AppTheme
import kotlinx.coroutines.flow.MutableStateFlow
import org.koin.android.ext.android.get
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

    override fun onCreate(savedInstanceState: Bundle?) {

        installSplashScreen()
        super.onCreate(savedInstanceState)

        init()
        enableEdgeToEdge()
        setContent { App() }
    }

    private fun init() {

        isAppOpen = true
        token = readFromDataStore(context, tokenKey) ?: unauthenticatedToken
        isFirstLaunch = readFromDataStore(context, isFirstLaunchKey)?.toBooleanStrictOrNull() ?: true // todo remove this var on resume subscription requirement 310125
        onboardingProgressRoute = readFromDataStore(context, onboardingKey) ?: OnboardingTextToSpeechScreenRoute

        isNotificationPermissionGranted.value =
            Build.VERSION.SDK_INT < 33 || // realtime permission required if sdk >= 32
                    ContextCompat.checkSelfPermission( // permission already granted
                        context,
                        Manifest.permission.POST_NOTIFICATIONS) == PackageManager.PERMISSION_GRANTED

        // dismiss notifications on launch
        val isLaunchFromNotification = intent.extras?.getBoolean(isLaunchFromNotification) ?: false
        if (isLaunchFromNotification) { cancelAllNotifications(context) }
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

        if (isNotificationPermissionGranted.value) return

        // launch system permission request ui
        requestPermissionLauncher
            .launch(Manifest.permission.POST_NOTIFICATIONS)
    }

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()) { isGranted ->
                if (isGranted) {
                    isNotificationPermissionGranted.value = true
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

    // initialize billing client
    viewModel.initBilling(
        koinInject<BillingClientImpl> { parametersOf(context) })

    KoinContext {
        AppTheme(viewModel.isDarkMode) {
            Scaffold { padding ->
                Modifier.padding(padding) // not used
                Navigation(rememberNavController())
            }
        }
    }
}
