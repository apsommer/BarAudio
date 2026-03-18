package com.sommerengineering.baraudio.navigation

import androidx.compose.runtime.Composable
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.sommerengineering.baraudio.MainViewModel
import com.sommerengineering.baraudio.login.LoginScreen
import com.sommerengineering.baraudio.messages.MessagesScreen
import com.sommerengineering.baraudio.uitls.AppOnboardingRoute
import com.sommerengineering.baraudio.uitls.SetupOnboardingRoute
import com.sommerengineering.baraudio.uitls.LoginScreenRoute
import com.sommerengineering.baraudio.uitls.MessagesScreenRoute
import com.sommerengineering.baraudio.uitls.OnboardingTextToSpeechScreenRoute

@Composable
fun MainNavigation(
    viewModel: MainViewModel) {

    val controller = rememberNavController()

    // determine start destination
    val isOnboardingComplete = viewModel.isOnboardingComplete
    val startDestination = when {
        Firebase.auth.currentUser == null -> LoginScreenRoute
        !isOnboardingComplete -> OnboardingTextToSpeechScreenRoute
        else -> MessagesScreenRoute
    }

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

        // app onboarding
        composable(AppOnboardingRoute) {
            AppOnboardingNavigation(
                viewModel = viewModel)
        }

        // messages screen
        composable(MessagesScreenRoute) {
            MessagesScreen(
                viewModel = viewModel,
                onSignOut = {
                    viewModel.signOut()
                    controller.navigate(LoginScreenRoute) {
                        popUpTo(MessagesScreenRoute) { inclusive = true }
                }},
                onLaunchSetupOnboarding = {
                    controller.navigate(SetupOnboardingRoute)
                })
        }

        // setup webhook onboarding
        composable(SetupOnboardingRoute) {
            SetupWebhookNavigation(
                onClose = { controller.navigate(MessagesScreenRoute) {
                    popUpTo(SetupOnboardingRoute) { inclusive = true }
                }})
        }

    }
}

