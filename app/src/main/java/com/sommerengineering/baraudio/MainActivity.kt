package com.sommerengineering.baraudio

import android.content.res.Configuration.UI_MODE_NIGHT_NO
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.os.Bundle
import android.util.Log
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.rememberNavController
import com.google.firebase.Firebase
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.auth.auth
import com.sommerengineering.baraudio.theme.AppTheme
import org.koin.androidx.compose.koinViewModel



class MainActivity : ComponentActivity() {

    private lateinit var auth: FirebaseAuth

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        auth = Firebase.auth
        setContent {
            App(auth)
        }
    }

    override fun onStart() {
        super.onStart()
        val currentUser = auth.currentUser
        Log.d(TAG, "onStart: uid: " + currentUser?.uid)
    }
}

@Composable
fun App(
    auth: FirebaseAuth
) {
    AppTheme {

        // initialize
        val navController = rememberNavController()
        val viewModel: MainViewModel = koinViewModel()

        Scaffold { padding ->
            Navigation(
                auth,
                navController)
            Modifier.padding(padding)
        }
    }
}