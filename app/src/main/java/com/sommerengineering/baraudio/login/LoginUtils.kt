package com.sommerengineering.baraudio.login

import android.content.Context
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.navigation.NavHostController
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateManager
import com.google.android.play.core.appupdate.AppUpdateManagerFactory
import com.google.android.play.core.appupdate.AppUpdateOptions
import com.google.android.play.core.install.model.AppUpdateType
import com.google.android.play.core.install.model.UpdateAvailability
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.sommerengineering.baraudio.LoginScreenRoute
import com.sommerengineering.baraudio.MainActivity
import com.sommerengineering.baraudio.MainViewModel
import com.sommerengineering.baraudio.MessagesScreenRoute
import com.sommerengineering.baraudio.OnboardingTextToSpeechScreenRoute
import com.sommerengineering.baraudio.isOnboardingComplete
import com.sommerengineering.baraudio.isUpdateRequired
import com.sommerengineering.baraudio.messages.dbListener
import com.sommerengineering.baraudio.messagesNode
import com.sommerengineering.baraudio.utils.getDatabaseReference
import com.sommerengineering.baraudio.utils.logException
import com.sommerengineering.baraudio.utils.logMessage
import com.sommerengineering.baraudio.utils.signOut
import com.sommerengineering.baraudio.utils.writeTokenToDatabase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch

fun onAuthentication(
    context: Context,
    viewModel: MainViewModel,
    controller: NavHostController) {

    // reset dark mode to previous preference, if available
    viewModel.setUiMode(context)

    // write to database, if needed
    writeTokenToDatabase()

    // navigate to next destination
    val nextDestination =
        if (!isOnboardingComplete) OnboardingTextToSpeechScreenRoute
        else MessagesScreenRoute

    controller.navigate(nextDestination) {
        popUpTo(LoginScreenRoute) { inclusive = true }
    }
}

fun onSignOut(
    credentialManager: CredentialManager,
    controller: NavHostController,
    viewModel: MainViewModel,
    context: Context) {

    // user already signed-out
    if (Firebase.auth.currentUser == null) { return }

    // sign-out firebase
    signOut()

    CoroutineScope(Dispatchers.Main).launch {
        credentialManager.clearCredentialState(
            ClearCredentialStateRequest())
    }

    viewModel.messages.clear()

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

fun checkForcedUpdate(
    credentialManager: CredentialManager,
    controller: NavHostController,
    viewModel: MainViewModel,
    context: Context) {

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
                return@addOnSuccessListener
            }

            val priority = updateInfo.updatePriority()
            logMessage("Update available with priority: $priority")
            if (4 >= priority) {
                logMessage("  Not forced, continue normal app behavior ...")
                return@addOnSuccessListener
            }

            logMessage("  Force update, block app until update installed")
            isUpdateRequired = true

            // sign out, if needed
            onSignOut(
                credentialManager = credentialManager,
                controller = controller,
                viewModel = viewModel,
                context = context)

            // launch system update flow ui
            onForcedUpdate(
                updateManager,
                updateInfo,
                context)
        }

        .addOnFailureListener { exception ->

            // skip exception log for debug build
            if (exception.message?.contains("The app is not owned") == true) return@addOnFailureListener
            logException(exception)
        }
}

fun onForcedUpdate(
    updateManager: AppUpdateManager,
    updateInfo: AppUpdateInfo,
    context: Context) {

    // launch system update flow ui
    updateManager.startUpdateFlowForResult(
        updateInfo,
        (context as MainActivity).updateLauncher,
        AppUpdateOptions.newBuilder(AppUpdateType.IMMEDIATE).build())
}