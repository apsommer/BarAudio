package com.sommerengineering.baraudio

import android.content.Context
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
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
import com.sommerengineering.baraudio.login.OnboardingScreen
import com.sommerengineering.baraudio.messages.MessagesScreen
import com.sommerengineering.baraudio.messages.dbListener
import org.koin.androidx.compose.koinViewModel

// routes
const val LoginScreenRoute = "LoginScreen"
const val OnboardingTextToSpeechScreenRoute = "OnboardingTextToSpeechScreen"
const val OnboardingNotificationsScreenRoute = "OnboardingNotificationsScreen"
const val OnboardingWebhookScreenRoute = "OnboardingWebhookScreen"
const val MessagesScreenRoute = "MessagesScreen"

@Composable
fun Navigation(
    controller: NavHostController) {

    val context = LocalContext.current
    val viewModel: MainViewModel = koinViewModel(viewModelStoreOwner = context as MainActivity)

    // animate screen transitions
    val fadeIn = fadeIn(spring(stiffness = 10f))
    val fadeOut = fadeOut(spring(stiffness = 10f))

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

        // onboarding screen: text-to-speech
        composable(
            route = OnboardingTextToSpeechScreenRoute,
            enterTransition = { fadeIn },
            exitTransition = { fadeOut }) {

            OnboardingScreen(
                viewModel = viewModel,
                pageNumber = 0,
                onNextClick = {
                    logMessage("Next click ...")
                    controller.navigate(OnboardingNotificationsScreenRoute)
                        // todo popUpTo inclusive?
                })
        }

        // onboarding screen: notifications
        composable(
            route = OnboardingNotificationsScreenRoute,
            enterTransition = { fadeIn },
            exitTransition = { fadeOut }) {

            OnboardingScreen(
                viewModel = viewModel,
                pageNumber = 1,
                onNextClick = {
                    logMessage("Next click ...")
                })
        }

        // login screen
        composable(
            route = LoginScreenRoute,
            enterTransition = { fadeIn },
            exitTransition = { fadeOut }) {

            LoginScreen(
                onAuthentication = {
                    onAuthentication(
                        context = context,
                        viewModel = viewModel,
                        controller = controller)
                },

                // block login attempt if update required
                onForceUpdate = {
                    onForceUpdate(
                        context = context,
                        viewModel = viewModel,
                        controller = controller)
                })
        }

        // messages screen
        composable(
            route = MessagesScreenRoute,
            enterTransition = { fadeIn },
            exitTransition = { fadeOut }) {

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
fun getStartDestination(): String {

//    if (Firebase.auth.currentUser == null) {
//        return LoginScreenRoute
//    }

    if (!isOnboardingComplete) {
        return OnboardingTextToSpeechScreenRoute
    }

    // log for development
    logMessage("Authentication skipped, user already signed-in")
    logMessage("    uid: ${Firebase.auth.currentUser?.uid}")
    logMessage("  token: $token")

    return MessagesScreenRoute
}

fun onAuthentication(
    context: Context,
    viewModel: MainViewModel,
    controller: NavHostController) {

    // reset dark mode to previous preference, if available
    viewModel.setUiMode(context)

    // request notification permission, does nothing if already granted
    (context as MainActivity).requestNotificationPermission()

    // write user:token pair to database, if needed
    writeTokenToDatabase()

    // navigate to messages screen
    controller.navigate(MessagesScreenRoute) {
        popUpTo(LoginScreenRoute) { inclusive = true }
    }
}

fun onSignOut(
    context: Context,
    viewModel: MainViewModel,
    controller: NavHostController) {

    // user already signed-out
    if (Firebase.auth.currentUser == null) { return }

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

fun onForceUpdate(
    context: Context,
    viewModel: MainViewModel,
    controller: NavHostController) {

    // sign out, if needed
    onSignOut(
        context = context,
        viewModel = viewModel,
        controller = controller)

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

            logMessage("  Force update, block app until update installed")
            isUpdateRequired = true

            // launch update flow ui
            updateManager.startUpdateFlowForResult(
                updateInfo,
                (context as MainActivity).updateLauncher,
                AppUpdateOptions.newBuilder(AppUpdateType.IMMEDIATE).build())
        }

        .addOnFailureListener { exception ->

            // skip exception log for debug build
            if (exception.message
                ?.contains("The app is not owned") == true)
                    { return@addOnFailureListener }
            logException(exception)
        }
}

