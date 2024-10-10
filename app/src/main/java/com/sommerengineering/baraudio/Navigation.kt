package com.sommerengineering.baraudio

import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.navOptions
import com.sommerengineering.baraudio.alerts.AlertsScreen
import com.sommerengineering.baraudio.alerts.AlertsState
import com.sommerengineering.baraudio.login.LoginScreen
import com.sommerengineering.baraudio.login.LoginState

// routes
val LoginScreenRoute = "LoginScreen"
val AlertScreenRoute = "AlertScreen"

@Composable
fun Navigation(
    controller: NavHostController,
    modifier: Modifier = Modifier
) {

    // host is container for current destination
    NavHost(
        navController = controller,
        startDestination = LoginScreenRoute,
        modifier = modifier
    ) {
        composable(
            route = LoginScreenRoute) {
            LoginScreen(
                onClickLoginWithGoogle = {
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