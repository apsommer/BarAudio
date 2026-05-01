package com.sommerengineering.signalvoice.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.sommerengineering.signalvoice.MainViewModel
import com.sommerengineering.signalvoice.login.LoginScreen
import com.sommerengineering.signalvoice.messages.MessagesScreen
import com.sommerengineering.signalvoice.uitls.AppOnboardingRoute
import com.sommerengineering.signalvoice.uitls.LoginRoute
import com.sommerengineering.signalvoice.uitls.MessagesRoute
import com.sommerengineering.signalvoice.uitls.SetupOnboardingRoute

@Composable
fun MainNavigation(
    viewModel: MainViewModel
) {

    val context = LocalContext.current
    val controller = rememberNavController()

    // start destination
    val isOnboardingComplete = viewModel.isOnboardingComplete
    val startDestination = when {
        Firebase.auth.currentUser == null -> LoginRoute
        !isOnboardingComplete -> AppOnboardingRoute
        else -> MessagesRoute
    }

    // post login destination
    val postLoginDestination =
        if (isOnboardingComplete) MessagesRoute
        else AppOnboardingRoute

    // guest session
    val onNavigateToLogin = {
        controller.navigate(LoginRoute) {
            popUpTo(controller.graph.startDestinationId) {
                inclusive = true
            }
        }
    }

    NavHost(
        navController = controller,
        startDestination = startDestination
    ) {

        // login screen
        composable(LoginRoute) {
            LoginScreen(
                viewModel = viewModel,
                onAuthentication = {
                    controller.navigate(postLoginDestination) {
                        popUpTo(LoginRoute) { inclusive = true }
                    }
                })
        }

        // app onboarding
        AppOnboardingNavigation(
            controller = controller,
            context = context,
            viewModel = viewModel
        )

        // messages screen
        composable(MessagesRoute) {
            MessagesScreen(
                viewModel = viewModel,
                onSignOut = {
                    viewModel.signOut()
                    controller.navigate(LoginRoute) {
                        popUpTo(MessagesRoute) { inclusive = true }
                    }
                },
                onLaunchWebhookOnboarding = {
                    controller.navigate(SetupOnboardingRoute)
                },
                onNavigateToLogin = onNavigateToLogin
            )
        }

        // setup webhook onboarding
        SetupWebhookNavigation(
            controller = controller,
            viewModel = viewModel,
            onClose = { controller.popBackStack() },
            onNavigateToLogin = onNavigateToLogin
        )
    }
}

