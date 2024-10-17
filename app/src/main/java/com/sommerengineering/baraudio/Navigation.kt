package com.sommerengineering.baraudio

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.google.firebase.auth.FirebaseAuth
import com.sommerengineering.baraudio.alerts.AlertsScreen
import com.sommerengineering.baraudio.login.LoginScreen

// routes
val LoginScreenRoute = "LoginScreen"
val AlertScreenRoute = "AlertScreen"

@Composable
fun Navigation(
//    auth: FirebaseAuth,
    controller: NavHostController
) {

    // host is container for current destination
    NavHost(
        navController = controller,
        startDestination = LoginScreenRoute,
    ) {
        composable(
            route = LoginScreenRoute) {
            LoginScreen(
//                auth = auth,
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