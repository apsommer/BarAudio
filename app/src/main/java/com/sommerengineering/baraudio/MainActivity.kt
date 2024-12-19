package com.sommerengineering.baraudio

import android.Manifest
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
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.appupdate.AppUpdateOptions
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.sommerengineering.baraudio.theme.AppTheme
import org.koin.android.ext.android.get
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.KoinContext
import org.koin.compose.koinInject
import org.koin.core.parameter.parametersOf

var isAppOpen = false
var isUpdateRequired = false

class MainActivity : ComponentActivity() {

    val context = this

    override fun onCreate(savedInstanceState: Bundle?) {

        installSplashScreen()
        super.onCreate(savedInstanceState)

        // init
        isAppOpen = true
        get<TextToSpeechImpl>()
        token = readFromDataStore(context, tokenKey) ?: unauthenticatedToken

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

    fun requestNotificationPermission() {

        // realtime permission required if sdk >= 32
        if (Build.VERSION.SDK_INT < 33) return

        // permission already granted
        if (ContextCompat
            .checkSelfPermission(
                context,
                Manifest.permission.POST_NOTIFICATIONS)
                    == PackageManager.PERMISSION_GRANTED)
                        { return }

        // launch system permission request ui
        requestPermissionLauncher
            .launch(Manifest.permission.POST_NOTIFICATIONS)
    }

    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()) { isGranted ->
                if (isGranted) { initNotificationChannel() }
                else { /** ui that explains this permission is required to run app */ }
            }

    private fun initNotificationChannel() {

        // create channel
        val channel = NotificationChannel(
            getString(R.string.notification_channel_id),
            getString(R.string.notification_channel_name),
            NotificationManager.IMPORTANCE_DEFAULT) // >= DEFAULT to show in status bar

        channel.description =
            getString(R.string.notification_channel_description)

        // assign group
        val groupId = getString(R.string.notification_channel_group_id)
        channel.group = groupId
        
        // register with system
        val notificationManager =
            getSystemService(Service.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager
            .createNotificationChannelGroup(
                NotificationChannelGroup(
                    groupId,
                    getString(R.string.notification_channel_group_name)))
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
            Scaffold(
                topBar = {
                    // todo internet connection banner
                }) { padding ->
                Modifier.padding(padding) // not used
                Navigation(rememberNavController())
            }
        }
    }
}
