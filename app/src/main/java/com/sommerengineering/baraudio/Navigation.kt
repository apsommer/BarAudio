package com.sommerengineering.baraudio

import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.ui.platform.LocalContext
import androidx.credentials.CredentialManager
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.sommerengineering.baraudio.login.LoginScreen
import com.sommerengineering.baraudio.login.OnboardingScreen
import com.sommerengineering.baraudio.login.checkForcedUpdate
import com.sommerengineering.baraudio.login.onAuthentication
import com.sommerengineering.baraudio.login.onSignOut
import com.sommerengineering.baraudio.messages.MessagesScreen
import kotlinx.coroutines.flow.collect
import kotlinx.coroutines.flow.onEach
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

            // request notification permission, if needed
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

fun getStartDestination(): String {

    // skip login screen if user already authenticated
    if (Firebase.auth.currentUser == null) return LoginScreenRoute
    if (onboardingProgressRoute != OnboardingCompleteRoute) return onboardingProgressRoute

    // log for development
    logMessage("User already authenticated, sign-in flow skipped.")
    logMessage("    uid: ${Firebase.auth.currentUser?.uid}")
    logMessage("  token: $token")

    return MessagesScreenRoute
}
