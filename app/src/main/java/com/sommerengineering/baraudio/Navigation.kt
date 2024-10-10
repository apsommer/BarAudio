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

@Composable
fun Navigation(
    controller: NavHostController,
    modifier: Modifier = Modifier
) {

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
                    controller.navigate(AlertsState.route) {
                        popUpTo(LoginState.route) {
                            inclusive = true
                        }
                    }
                }
            )
        }
        composable(
            route = AlertsState.route) {
            AlertsScreen()
        }
    }

}