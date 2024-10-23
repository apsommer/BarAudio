package com.sommerengineering.baraudio

import android.Manifest
import android.app.NotificationChannel
import android.app.NotificationManager
import android.app.Service
import android.content.pm.PackageManager
import android.os.Build
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.activity.result.contract.ActivityResultContracts
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.core.content.ContextCompat
import androidx.navigation.compose.rememberNavController
import com.sommerengineering.baraudio.theme.AppTheme

class MainActivity : ComponentActivity() {

    // initialize system permission request ui
    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()) { isGranted ->

            if (isGranted) { initNotificationChannel() }
            else { /** todo ui that explains this permission is required to run baraudio */ }
        }

    fun requestRealtimeNotificationPermission() {

        // realtime permission only required if sdk >= 32
        if (Build.VERSION.SDK_INT < 33) return

        // permission already granted
        if (ContextCompat.checkSelfPermission
                (this, Manifest.permission.POST_NOTIFICATIONS)
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
        // todo set system-wide category for do not disturb visibility

        // register channel with system
        val notificationManager = getSystemService(Service.NOTIFICATION_SERVICE) as NotificationManager
        notificationManager.createNotificationChannel(channel)

        logMessage("Notification channel registered")
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            App()
        }
    }

    override fun onStart() {
        super.onStart()

        // todo remove on production release, not necessary
        logMessage("MainActivity started")
        initFirebase()
    }

    override fun onResume() {
        super.onResume()

        // todo remove all notifications
    }
}

@Composable
fun App() {
    AppTheme {

        // initialize
        val navController = rememberNavController()

        Scaffold { padding ->
            Navigation(navController)
            Modifier.padding(padding)
        }
    }
}
