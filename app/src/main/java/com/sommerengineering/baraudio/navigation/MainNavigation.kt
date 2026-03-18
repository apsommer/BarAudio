package com.sommerengineering.baraudio.navigation

import android.Manifest
import android.os.Build
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.remember
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.sommerengineering.baraudio.MainActivity
import com.sommerengineering.baraudio.MainViewModel
import com.sommerengineering.baraudio.login.LoginScreen
import com.sommerengineering.baraudio.messages.MessagesScreen
import com.sommerengineering.baraudio.uitls.LoginScreenRoute
import com.sommerengineering.baraudio.uitls.MessagesScreenRoute
import com.sommerengineering.baraudio.uitls.OnboardingNotificationsScreenRoute
import com.sommerengineering.baraudio.uitls.OnboardingTextToSpeechScreenRoute
import com.sommerengineering.baraudio.uitls.OnboardingWebhookScreenRoute
import com.sommerengineering.baraudio.navigation.OnboardingMode.AppOnboarding

@Composable
fun MainNavigation(
    viewModel: MainViewModel) {

    val context = LocalContext.current
    val controller = rememberNavController()

    // determine start destination
    val isOnboardingComplete = viewModel.isOnboardingComplete
    val startDestination =
        if (Firebase.auth.currentUser == null) LoginScreenRoute
        else if (!isOnboardingComplete) OnboardingTextToSpeechScreenRoute
        else MessagesScreenRoute

    NavHost(
        navController = controller,
        startDestination = startDestination) {

        // login screen
        composable(LoginScreenRoute) {
            LoginScreen(
                viewModel = viewModel,
                onAuthentication = {
                    val nextDestination = viewModel.postLoginDestination
                    controller.navigate(nextDestination) {
                        popUpTo(LoginScreenRoute) { inclusive = true }
                    }
                })
        }

        // onboarding screen: text-to-speech
        composable(OnboardingTextToSpeechScreenRoute) {
            OnboardingScreen(
                viewModel = viewModel,
                onboardingMode = AppOnboarding,
                pageNumber = 0,
                onNextClick = { controller.navigate(OnboardingNotificationsScreenRoute) })
        }

        // onboarding screen: notifications
        composable(OnboardingNotificationsScreenRoute) {

            // ask for permission again if the first request is declined
            val areNotificationsEnabled = viewModel.areNotificationsEnabled
            val count = remember { mutableIntStateOf(0) }

            // navigate forward if notifications are granted
            LaunchedEffect(areNotificationsEnabled) {
                if (areNotificationsEnabled && Build.VERSION.SDK_INT >= 33) {
                    controller.navigate(OnboardingWebhookScreenRoute)
                }
            }

            OnboardingScreen(
                viewModel = viewModel,
                onboardingMode = AppOnboarding,
                pageNumber = 1,
                onNextClick = {
                    if (Build.VERSION.SDK_INT >= 33 && 2 > count.intValue) {
                        (context as MainActivity).requestNotificationPermissionLauncher
                            .launch(Manifest.permission.POST_NOTIFICATIONS)
                        count.intValue ++
                    }
                    else { controller.navigate(OnboardingWebhookScreenRoute) }
                })
        }

        // onboarding screen: webhook
        composable(OnboardingWebhookScreenRoute) {
            OnboardingScreen(
                viewModel = viewModel,
                onboardingMode = AppOnboarding,
                pageNumber = 2,
                onNextClick = {
                    viewModel.updateOnboarding(true)
                    controller.navigate(MessagesScreenRoute) {
                        popUpTo(OnboardingTextToSpeechScreenRoute) { inclusive = true }
                    }
                })
        }

        // messages screen
        composable(MessagesScreenRoute) {
            MessagesScreen(
                viewModel = viewModel,
                onSignOut = {
                    viewModel.signOut()
                    controller.navigate(LoginScreenRoute) {
                        popUpTo(MessagesScreenRoute) { inclusive = true }
                    }
                })
        }
    }
}

