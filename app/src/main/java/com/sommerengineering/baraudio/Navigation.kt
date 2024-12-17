package com.sommerengineering.baraudio

import android.content.Context
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.appupdate.AppUpdateOptions
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import com.sommerengineering.baraudio.login.LoginScreen
import com.sommerengineering.baraudio.messages.MessagesScreen
import com.sommerengineering.baraudio.messages.dbListener
import org.koin.androidx.compose.koinViewModel

// routes
const val LoginScreenRoute = "LoginScreen"
const val MessagesScreenRoute = "MessagesScreen"

@Composable
fun Navigation(
    controller: NavHostController) {

    val context = LocalContext.current
    val viewModel: MainViewModel = koinViewModel(viewModelStoreOwner = context as MainActivity)

    // force update, if needed
    LaunchedEffect(Unit){
        onForceUpdate(
            context,
            viewModel,
            controller)
    }

    NavHost(
        navController = controller,
        startDestination = getStartDestination()) {

        // login screen
        composable(
            route = LoginScreenRoute) {
            LoginScreen(
                onAuthentication = {
                    onAuthentication(
                        context = context,
                        viewModel = viewModel,
                        controller = controller)
                },
                onForceUpdate = {
                    onForceUpdate(
                        context = context,
                        viewModel = viewModel,
                        controller = controller)
                }
            )
        }

        // messages screen
        composable(
            route = MessagesScreenRoute) {
            MessagesScreen(
                onSignOut = {
                    onSignOut(
                        controller = controller,
                        viewModel = viewModel,
                        context = context)
                })
        }
    }
}

// skip login screen if user already authenticated
fun getStartDestination() =

    if (Firebase.auth.currentUser != null) {
        logMessage("Authentication skipped, user already signed-in")
        logMessage("    uid: ${Firebase.auth.currentUser?.uid}")
        logMessage("  token: $token")
        MessagesScreenRoute }
    else LoginScreenRoute

fun onAuthentication(
    context: Context,
    viewModel: MainViewModel,
    controller: NavHostController) {

    // reset dark mode to previous preference, if available
    viewModel.setUiMode(context)

    // request notification permission, does nothing if already granted
    (context as MainActivity).requestNotificationPermission()

    // write user:token pair to database, if needed
    validateToken()

    // navigate to messages screen
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
    // todo can remove this if webhook has token instead of uid
    //  then can simplify further in force update by removing currentUser != null conditional
    getDatabaseReference(messagesNode)
        .removeEventListener(dbListener)

    // navigate to login screen
    controller.navigate(LoginScreenRoute) {
        popUpTo(MessagesScreenRoute) { inclusive = true }
    }
}

// todo refactor updateManager out of listener, should be attached only once
fun onForceUpdate(
    context: Context,
    viewModel: MainViewModel,
    controller: NavHostController) {

    val updateManager =
        AppUpdateManagerFactory
            .create(context)

    // request update from play store
    updateManager
        .appUpdateInfo
        .addOnSuccessListener { updateInfo ->

            // check if update available
            val availability = updateInfo.updateAvailability()
            if (availability == UpdateAvailability.UNKNOWN ||
                availability == UpdateAvailability.UPDATE_NOT_AVAILABLE) {
                    logMessage("Update not available")
                    return@addOnSuccessListener
            }

            val priority = updateInfo.updatePriority()
            logMessage("Update available with priority: $priority")
            if (priority < 5) {
                logMessage("  Not forced, continue normal app behavior ...")
                return@addOnSuccessListener
            }

            logMessage("  Force update, blocking app until update installed")
            isUpdateRequired = true

            // sign out, if needed
            if (Firebase.auth.currentUser != null) {
                onSignOut(
                    controller,
                    viewModel,
                    context)
            }

            // launch update flow ui
            updateManager.startUpdateFlowForResult(
                updateInfo,
                (context as MainActivity).updateLauncher,
                AppUpdateOptions.newBuilder(AppUpdateType.IMMEDIATE).build())
        }
        .addOnFailureListener {
            logException(it)
        }
}

