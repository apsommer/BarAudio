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
import androidx.lifecycle.Lifecycle
import androidx.lifecycle.compose.LifecycleEventEffect
import androidx.navigation.NavHostController
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.sommerengineering.baraudio.hilt.writeToDataStore
import com.sommerengineering.baraudio.login.LoginScreen
import com.sommerengineering.baraudio.login.OnboardingScreen
import com.sommerengineering.baraudio.login.checkForcedUpdate
import com.sommerengineering.baraudio.login.onAuthentication
import com.sommerengineering.baraudio.login.onSignOut
import com.sommerengineering.baraudio.messages.MessagesScreen

@Composable
fun Navigation(
    controller: NavHostController,
    viewModel: MainViewModel) {

    val context = LocalContext.current

    // animate screen transitions
    val fadeIn = fadeIn(spring(stiffness = 10f))
    val fadeOut = fadeOut(spring(stiffness = 10f))

    // check for forced updated
    LaunchedEffect(Unit) {
        checkForcedUpdate(
            credentialManager = viewModel.credentialManager,
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
                viewModel = viewModel,
                onAuthentication = {
                    onAuthentication(
                        controller = controller)
                },
                onForceUpdate = {
                    checkForcedUpdate(
                        credentialManager = viewModel.credentialManager,
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
                isNextEnabled = viewModel.isTtsInit.collectAsState().value)
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

                    // request notification permission
                    if (Build.VERSION.SDK_INT >= 33 && 2 > count.intValue) {
                        (context as MainActivity).requestNotificationPermissionLauncher
                            .launch(Manifest.permission.POST_NOTIFICATIONS)
                        count.intValue ++
                    }
                    
                    else {
                        controller.navigate(OnboardingWebhookScreenRoute)
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
                    isOnboardingComplete = true

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
                areNotificationsEnabled =(context as MainActivity).areNotificationsEnabled()
            }

            MessagesScreen(
                viewModel = viewModel,
                onSignOut = {
                    onSignOut(
                        credentialManager = viewModel.credentialManager,
                        controller = controller)
                })
        }
    }
}

fun getStartDestination(): String {

    // skip login screen if user already authenticated
    if (Firebase.auth.currentUser == null) return LoginScreenRoute

    // skip onboarding if user already completed
    if (!isOnboardingComplete) return OnboardingTextToSpeechScreenRoute
    return MessagesScreenRoute
}
