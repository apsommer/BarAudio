package com.sommerengineering.baraudio

import android.Manifest
import android.app.NotificationChannel
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
import com.sommerengineering.baraudio.login.BillingClientImpl
import com.sommerengineering.baraudio.theme.AppTheme
import org.koin.android.ext.android.get
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.KoinContext
import org.koin.compose.koinInject
import org.koin.core.parameter.parametersOf

var isAppBackground = false
var isAppForeground = false

class MainActivity : ComponentActivity() {

    val context = this

    // initialize system permission request ui
    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()) { isGranted ->

            if (isGranted) { initNotificationChannel() }
            else { /** todo ui that explains this permission is required to run app */ }
        }

    fun requestRealtimeNotificationPermission() {

        // realtime permission only required if sdk >= 32
        if (Build.VERSION.SDK_INT < 33) return

        // permission already granted
        if (ContextCompat.checkSelfPermission
                (context, Manifest.permission.POST_NOTIFICATIONS)
                    == PackageManager.PERMISSION_GRANTED) return

        // launch system permission request ui
        requestPermissionLauncher.launch(Manifest.permission.POST_NOTIFICATIONS)
    }

    private fun initNotificationChannel() {

        // build channel
        val id = getString(R.string.notification_channel_id)
        val name = getString(R.string.notification_channel_name)
        val description = getString(R.string.notification_channel_description)
        val importance = NotificationManager.IMPORTANCE_DEFAULT // >= DEFAULT to show in status bar
        val channel = NotificationChannel(id, name, importance)
        channel.description = description

        // todo channel group shown as "other" in settings, change this to something better

        // todo set system-wide category for do not disturb visibility
        //  https://developer.android.com/develop/ui/views/notifications/build-notification#system-category

        // register channel with system
        val notificationManager = getSystemService(Service.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)

        logMessage("Notification channel registered")
    }

    override fun onCreate(savedInstanceState: Bundle?) {

        installSplashScreen()
        super.onCreate(savedInstanceState)

        // init
        get<TextToSpeechImpl>()
        token = readFromDataStore(context, tokenKey) ?: unauthenticatedToken

        // dismiss notifications after launch
        val isLaunchFromNotification = intent.extras?.getBoolean(isLaunchFromNotification) ?: false
        if (isLaunchFromNotification) { cancelAllNotifications(context) }

        enableEdgeToEdge()
        setContent { App() }
    }

    override fun onStart() {
        super.onStart()
        isAppBackground = true
    }

    override fun onResume() {
        super.onResume()
        isAppForeground = true
        cancelAllNotifications(context)
    }

    override fun onPause() {
        super.onPause()
        isAppForeground = false
    }

    override fun onDestroy() {
        super.onDestroy()
        isAppBackground = false
    }
}

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
                Navigation(rememberNavController())
                Modifier.padding(padding)
            }
        }
    }
}

fun cancelAllNotifications(
    context: Context) =
        NotificationManagerCompat
            .from(context)
            .cancelAll()
