package com.sommerengineering.baraudio

import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.sommerengineering.baraudio.alerts.AlertsScreen
import com.sommerengineering.baraudio.login.LoginScreen
import com.sommerengineering.baraudio.login.LoginState
import com.sommerengineering.baraudio.login.googleSignIn

// routes
val LoginScreenRoute = "LoginScreen"
val AlertScreenRoute = "AlertScreen"

@Composable
fun Navigation(
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
                onSuccessSignIn = {
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