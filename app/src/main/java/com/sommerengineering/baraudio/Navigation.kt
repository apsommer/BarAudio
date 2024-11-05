package com.sommerengineering.baraudio

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.sommerengineering.baraudio.ui.MessagesScreen
import com.sommerengineering.baraudio.ui.LoginScreen
import com.sommerengineering.baraudio.ui.SettingsScreen

// routes
const val LoginScreenRoute = "LoginScreen"
const val AlertScreenRoute = "AlertScreen"
const val SettingsScreenRoute = "SettingsScreen"

@Composable
fun Navigation(
    controller: NavHostController
) {

    NavHost(
        navController = controller,
        startDestination = SettingsScreenRoute // getStartDestination()
    ) {
        composable(
            route = LoginScreenRoute) {
            LoginScreen(
                onAuthentication = {
                    controller.navigate(AlertScreenRoute) {
                        popUpTo(LoginScreenRoute) { inclusive = true }
                    }
                }
            )
        }
        composable(
            route = AlertScreenRoute) {
            MessagesScreen(
                onSettingsClick = {
                    controller.navigate(SettingsScreenRoute)
                }
            )
        }
        composable(
            route = SettingsScreenRoute) {
            SettingsScreen(
                onBackClicked = {
                    controller.navigateUp()
                }
            )
        }
    }
}

fun getStartDestination(): String {

    var startDestination = LoginScreenRoute

    // skip login screen if user already signed-in
    if (Firebase.auth.currentUser != null) {
        startDestination = AlertScreenRoute
        logMessage("Firebase authenticated, user already signed-in") }

    return startDestination
}