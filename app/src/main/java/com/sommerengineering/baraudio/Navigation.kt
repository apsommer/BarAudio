package com.sommerengineering.baraudio

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.sommerengineering.baraudio.alerts.AlertsScreen
import com.sommerengineering.baraudio.login.LoginScreen

// routes
const val LoginScreenRoute = "LoginScreen"
const val AlertScreenRoute = "AlertScreen"

@Composable
fun Navigation(
    controller: NavHostController
) {

    // host is container for current destination
    NavHost(
        navController = controller,
        startDestination = getStartDestination() // LoginScreenRoute
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