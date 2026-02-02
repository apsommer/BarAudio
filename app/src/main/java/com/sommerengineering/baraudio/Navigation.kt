package com.sommerengineering.baraudio

import android.Manifest
import android.os.Build
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.credentials.CredentialManager
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
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
import com.sommerengineering.baraudio.utils.logMessage
import com.sommerengineering.baraudio.utils.token
import com.sommerengineering.baraudio.utils.writeToDataStore

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
            credentialManager = credentialManager,
            controller = controller,
            viewModel = viewModel,
            context = context)
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
                    controller.navigate(OnboardingNotificationsScreenRoute)
                },
                isNextEnabled = viewModel.tts.isInit.collectAsState().value)
        }

        // onboarding screen: notifications
        composable(
            route = OnboardingNotificationsScreenRoute,
            enterTransition = { fadeIn },
            exitTransition = { fadeOut }) {

            // ask for permission again if the first request is declined
            val count = remember { mutableIntStateOf(0) }

            // navigate forward
            LaunchedEffect(areNotificationsEnabled) {
                if (areNotificationsEnabled && Build.VERSION.SDK_INT >= 33) {
                    controller.navigate(OnboardingWebhookScreenRoute)
                }
            }

            OnboardingScreen(
                viewModel = viewModel,
                pageNumber = 1,
                onNextClick = {

                    if (Build.VERSION.SDK_INT >= 33 && 2 > count.intValue) {
                        context.requestNotificationPermissionLauncher
                            .launch(Manifest.permission.POST_NOTIFICATIONS)
                        count.intValue ++
                    }

                    else {
                        controller.navigate(OnboardingWebhookScreenRoute)
                    }


                    if (context.areNotificationsEnabled() || 32 >= Build.VERSION.SDK_INT || count.intValue > 1) {
                        controller.navigate(OnboardingWebhookScreenRoute)

                    // request notification permission
                    } else {

                    }
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

                    // onboarding complete
                    writeToDataStore(context, onboardingKey, true.toString())

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

            // check for notification permission
            LifecycleEventEffect(Lifecycle.Event.ON_RESUME) {
                areNotificationsEnabled = context.areNotificationsEnabled()
            }

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

    // log for development
    logMessage("User already authenticated, sign-in flow skipped.")
    logMessage("    uid: ${Firebase.auth.currentUser?.uid}")
    logMessage("  token: ${token}")

    if (!isOnboardingComplete) return OnboardingTextToSpeechScreenRoute

    return MessagesScreenRoute
}
