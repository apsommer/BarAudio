package com.sommerengineering.baraudio

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
import android.Manifest

class MainActivity : ComponentActivity() {

    // initialize system permission request ui
    private val requestPermissionLauncher =
        registerForActivityResult(
            ActivityResultContracts.RequestPermission()) { isGranted ->

            if (!isGranted) {
                // todo ui that explains this permission is required to run baraudio
            }
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

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            App()
        }
    }

    override fun onStart() {
        super.onStart()
        getFirebaseToken()

        // todo setup notification channel manually for first time permission allowance flow
        //  accept permission -> fcm will not show until app restarted once
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
