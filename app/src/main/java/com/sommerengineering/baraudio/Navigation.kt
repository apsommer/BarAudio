package com.sommerengineering.baraudio

import android.content.Context
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.platform.LocalContext
import androidx.credentials.ClearCredentialStateRequest
import androidx.credentials.CredentialManager
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.google.android.play.core.appupdate.AppUpdateInfo
import com.google.android.play.core.appupdate.AppUpdateManager
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
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
import kotlinx.coroutines.launch
import org.koin.androidx.compose.koinViewModel
import org.koin.compose.koinInject

@Composable
fun Navigation(
    controller: NavHostController) {

    val context = LocalContext.current
    val viewModel: MainViewModel = koinViewModel(viewModelStoreOwner = context as MainActivity)
    val credentialManager = koinInject<CredentialManager>()

    // animate screen transitions
    val fadeIn = fadeIn(spring(stiffness = 10f))
    val fadeOut = fadeOut(spring(stiffness = 10f))

    // check for forced updated
    LaunchedEffect(Unit) {
        checkForcedUpdate(
            credentialManager,
            controller,
            viewModel,
            context)
    }

    NavHost(
        navController = controller,
        startDestination = getStartDestination()) {

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
                onForceUpdate = {
                    checkForcedUpdate(
                        credentialManager = credentialManager,
                        controller = controller,
                        viewModel = viewModel,
                        context = context)
                })
        }

        // onboarding screen: text-to-speech
        composable(
            route = OnboardingTextToSpeechScreenRoute,
            enterTransition = { fadeIn },
            exitTransition = { fadeOut }) {

            OnboardingScreen(
                viewModel = viewModel,
                pageNumber = 0,
                onNextClick = {
                    writeToDataStore(context, onboardingKey, OnboardingNotificationsScreenRoute)
                    controller.navigate(OnboardingNotificationsScreenRoute)
                },
                isNextEnabled = viewModel.tts.isInit.collectAsState().value)
        }

        // onboarding screen: notifications
        composable(
            route = OnboardingNotificationsScreenRoute,
            enterTransition = { fadeIn },
            exitTransition = { fadeOut }) {

            LaunchedEffect(Unit) {
                isNotificationPermissionGranted
                    .onEach { isGranted ->
                        if (isGranted) {
                            writeToDataStore(context, onboardingKey, OnboardingWebhookScreenRoute)
                            controller.navigate(OnboardingWebhookScreenRoute)
                        }
                    }
                    .collect()
            }

            OnboardingScreen(
                viewModel = viewModel,
                pageNumber = 1,
                onNextClick = {
                    context.requestNotificationPermission()
                })
        }

        // onboarding screen: webhook
        composable(
            route = OnboardingWebhookScreenRoute,
            enterTransition = { fadeIn },
            exitTransition = { fadeOut }) {

            OnboardingScreen(
                viewModel = viewModel,
                pageNumber = 2,
                onNextClick = {
                    onboardingProgressRoute = OnboardingCompleteRoute
                    writeToDataStore(context, onboardingKey, onboardingProgressRoute)
                    controller.navigate(MessagesScreenRoute) {
                        popUpTo(OnboardingTextToSpeechScreenRoute) { inclusive = true }
                    }
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
                        credentialManager = credentialManager,
                        controller = controller,
                        viewModel = viewModel,
                        context = context)
                })
        }
    }
}

// skip login screen if user already authenticated
fun getStartDestination(): String {

    if (Firebase.auth.currentUser == null) {
        return LoginScreenRoute
    }

    if (onboardingProgressRoute != OnboardingCompleteRoute) {
        return onboardingProgressRoute
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

    // write user:token pair to database, if needed
    writeTokenToDatabase()

    // navigate to next destination
    val nextDestination =
        if (onboardingProgressRoute != OnboardingCompleteRoute) { onboardingProgressRoute }
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

            // sign out, if needed todo necessary?
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

