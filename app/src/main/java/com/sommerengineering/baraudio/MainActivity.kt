package com.sommerengineering.baraudio

import android.content.res.Configuration.UI_MODE_NIGHT_NO
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.activity.enableEdgeToEdge
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.sommerengineering.baraudio.alerts.AlertsScreen
import com.sommerengineering.baraudio.alerts.AlertsState
import com.sommerengineering.baraudio.login.LoginScreen
import com.sommerengineering.baraudio.login.LoginState
import com.sommerengineering.baraudio.theme.AppTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        setContent {
            App()
        }
    }
}

@Composable
fun App() {
    AppTheme {

        // create navigation controller
        val navController = rememberNavController()
        
        Scaffold { padding ->
            NavigationHost(navController)
            Modifier.padding(padding)
        }
    }
}

@Composable
fun NavigationHost(
    controller: NavHostController,
    modifier: Modifier = Modifier) {

    // host is container for current destination
    NavHost(
        navController = controller,
        startDestination = LoginState.route,
        modifier = modifier
    ) {
        composable(
            route = LoginState.route) {
            LoginScreen(
                onClickLoginWithGoogle = {
                    controller.navigate(AlertsState.route)
                }
            )
        }
        composable(
            route = AlertsState.route) {
            AlertsScreen()
        }
    }

}