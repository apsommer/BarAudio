package com.sommerengineering.baraudio

import androidx.compose.runtime.Composable
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.sommerengineering.baraudio.login.LoginScreen
import com.sommerengineering.baraudio.messages.MessagesScreen
import com.sommerengineering.baraudio.settings.SettingsScreen

// routes
const val LoginScreenRoute = "LoginScreen"
const val MessagesScreenRoute = "AlertScreen"
const val SettingsScreenRoute = "SettingsScreen"

@Composable
fun Navigation(
    controller: NavHostController) {

    NavHost(
        navController = controller,
        startDestination = getStartDestination()) {
        composable(
            route = LoginScreenRoute) {
            LoginScreen(
                onAuthentication = {
                    controller.navigate(MessagesScreenRoute) {
                        popUpTo(LoginScreenRoute) { inclusive = true }
                    }
                })
        }
        composable(
            route = MessagesScreenRoute) {
            MessagesScreen(
                onSettingsClick = { controller.navigate(SettingsScreenRoute) })
        }
        composable(
            route = SettingsScreenRoute) {
            SettingsScreen(
                onBackClicked = { controller.navigateUp() },
                onSignOut = {
                    signOut()
                    controller.navigate(LoginScreenRoute) {
                        popUpTo(MessagesScreenRoute) { inclusive = true }
                    }
                })
        }
    }
}

// skip login screen if user already authenticated
fun getStartDestination() =
    if (Firebase.auth.currentUser != null) { MessagesScreenRoute }
    else LoginScreenRoute


