package com.sommerengineering.baraudio

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.google.firebase.auth.FirebaseAuth
import com.sommerengineering.baraudio.alerts.AlertsScreen
import com.sommerengineering.baraudio.login.LoginScreen
import org.koin.compose.koinInject

// routes
const val LoginScreenRoute = "LoginScreen"
const val AlertScreenRoute = "AlertScreen"

@Composable
fun Navigation(
    controller: NavHostController
) {

    // skip login screen if user already signed-in
    val firebaseAuth: FirebaseAuth = koinInject()
    val startDestination = LoginScreenRoute
        if (firebaseAuth.currentUser != null) { AlertScreenRoute }
        else { LoginScreenRoute }

    // host is container for current destination
    NavHost(
        navController = controller,
        startDestination = LoginScreenRoute // startDestination, todo disable for testing
    ) {
        composable(
            route = LoginScreenRoute) {
            LoginScreen(
                onAuthentication = {
                    controller.navigate(AlertScreenRoute) {
                        popUpTo(LoginScreenRoute) {
                            inclusive = true
                        }
                    }
                }
            )
        }
        composable(
            route = AlertScreenRoute) {
            AlertsScreen()
        }
    }
}