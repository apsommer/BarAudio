package com.sommerengineering.baraudio

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.sommerengineering.baraudio.login.LoginScreen
import com.sommerengineering.baraudio.messages.MessagesScreen
import com.sommerengineering.baraudio.messages.dbListener
import com.sommerengineering.baraudio.settings.SettingsScreen
import org.koin.androidx.compose.koinViewModel

// routes
const val LoginScreenRoute = "LoginScreen"
const val MessagesScreenRoute = "AlertScreen"
const val SettingsScreenRoute = "SettingsScreen"

@Composable
fun Navigation(
    controller: NavHostController) {

    // inject viewmodel
    val context = LocalContext.current
    val viewModel: MainViewModel = koinViewModel(viewModelStoreOwner = context as MainActivity)

    NavHost(
        navController = controller,
        startDestination = getStartDestination()) {
        composable(
            route = LoginScreenRoute) {
            LoginScreen(
                onAuthentication = {
                    onAuthentication(
                        controller = controller,
                        viewModel = viewModel,
                        context = context) })
        }
        composable(
            route = MessagesScreenRoute) {
            MessagesScreen(
                onSettingsClick = {
                    controller.navigate(SettingsScreenRoute)
                })
        }
        composable(
            route = SettingsScreenRoute) {
            SettingsScreen(
                onBackClicked = { controller.navigateUp() },
                onSignOut = {
                    onSignOut(
                        controller = controller,
                        viewModel = viewModel,
                        context = context) })
        }
    }
}

fun onAuthentication(
    controller: NavHostController,
    viewModel: MainViewModel,
    context: Context) {

    viewModel.setUiMode(context)

    (context as MainActivity).requestRealtimeNotificationPermission()

    validateToken()

    // todo check billing status

    controller.navigate(MessagesScreenRoute) {
        popUpTo(LoginScreenRoute) { inclusive = true }
    }
}

fun onSignOut(
    controller: NavHostController,
    viewModel: MainViewModel,
    context: Context) {

    // sign-out firebase
    signOut()

    // reset dark mode to system default
    viewModel.setUiMode(context)

    // clear local cache by detaching database listener
    getDatabaseReference(messagesNode)
        .removeEventListener(dbListener)

    // navigate to login screen
    controller.navigate(LoginScreenRoute) {
        popUpTo(MessagesScreenRoute) { inclusive = true }
    }
}

// skip login screen if user already authenticated
fun getStartDestination() =

    if (Firebase.auth.currentUser != null) {
        logMessage("Authentication skipped, user signed-in: ${Firebase.auth.currentUser?.uid}")
        MessagesScreenRoute }
    else LoginScreenRoute








