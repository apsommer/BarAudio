package com.sommerengineering.baraudio.navigation

import androidx.compose.runtime.Composable
import androidx.compose.ui.platform.LocalContext
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
import com.sommerengineering.baraudio.uitls.LoginRoute
import com.sommerengineering.baraudio.uitls.MessagesRoute

@Composable
fun MainNavigation(
    viewModel: MainViewModel) {

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

    NavHost(
        navController = controller,
        startDestination = startDestination) {

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
            viewModel = viewModel)

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
                onLaunchSetupOnboarding = {
                    controller.navigate(SetupOnboardingRoute)
                })
        }

        // setup webhook onboarding
        SetupWebhookNavigation(
            controller = controller,
            viewModel = viewModel,
            onClose = { controller.popBackStack() })
    }
}

