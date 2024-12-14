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
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.appupdate.AppUpdateOptions
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability
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

    override fun onResume() {
        super.onResume()
        forceUpdate()
    }

    fun forceUpdate() {

        val updateManager =
            AppUpdateManagerFactory
                .create(context)

        // request update from play store
        updateManager
            .appUpdateInfo
            .addOnSuccessListener { appUpdateInfo ->

                // todo need to check priority level with .updatePriority(): 0 -> 5
                //  most updates will not be forced
                //  priority must be set with Play Developer API?

                if (appUpdateInfo.updateAvailability() != UpdateAvailability.UPDATE_AVAILABLE ||
                    !appUpdateInfo.isUpdateTypeAllowed(AppUpdateType.IMMEDIATE)) {

                    logMessage("Update not available")
                    return@addOnSuccessListener
                }

                // todo sign-out firebase ...
                isUpdateRequired = true

                // launch update flow ui
                updateManager.startUpdateFlowForResult(
                    appUpdateInfo,
                    updateLauncher,
                    AppUpdateOptions.newBuilder(AppUpdateType.IMMEDIATE).build())
            }
            .addOnFailureListener {
                logException(it)
            }
    }

    private val updateLauncher =

        registerForActivityResult(
            ActivityResultContracts.StartIntentSenderForResult()) { result ->

            if (result.resultCode != RESULT_OK) {

                logMessage("Update flow failed with code: ${result.resultCode}")
                // todo dialog that states update is required, then
                return@registerForActivityResult
            }

            isUpdateRequired = false
            logMessage("Update finished, does play restart app?")
            // since this update is immediate (not flexible) play updates then restarts app
            // todo check for stalled update? https://developer.android.com/guide/playcore/in-app-updates/kotlin-java#immediate
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
                else { /** todo ui that explains this permission is required to run app */ }
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
}

fun cancelAllNotifications(
    context: Context) =
    NotificationManagerCompat
        .from(context)
        .cancelAll()

// entry point /////////////////////////////////////////////////////////////////////////////////////

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
