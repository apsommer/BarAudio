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
import com.sommerengineering.baraudio.navigation.AppOnboardingNavigation
import com.sommerengineering.baraudio.uitls.AppOnboardingRoute
import com.sommerengineering.baraudio.uitls.SetupOnboardingRoute
import com.sommerengineering.baraudio.uitls.LoginScreenRoute
import com.sommerengineering.baraudio.uitls.MessagesScreenRoute
import com.sommerengineering.baraudio.uitls.OnboardingTextToSpeechScreenRoute

@Composable
fun MainNavigation(
    viewModel: MainViewModel) {

    val context = LocalContext.current
    val controller = rememberNavController()

    // start destination
    val isOnboardingComplete = viewModel.isOnboardingComplete
    val startDestination = when {
        Firebase.auth.currentUser == null -> LoginScreenRoute
        !isOnboardingComplete -> AppOnboardingRoute
        else -> MessagesScreenRoute
    }

    // post login destination
    val postLoginDestination =
        if (isOnboardingComplete) MessagesScreenRoute
        else AppOnboardingRoute

    NavHost(
        navController = controller,
        startDestination = startDestination) {

        // login screen
        composable(LoginScreenRoute) {
            LoginScreen(
                viewModel = viewModel,
                onAuthentication = {
                    controller.navigate(postLoginDestination) {
                        popUpTo(LoginScreenRoute) { inclusive = true }
                    }
                })
        }

        // app onboarding
        AppOnboardingNavigation(
            controller = controller,
            context = context,
            viewModel = viewModel)

        // messages screen
        composable(MessagesScreenRoute) {
            MessagesScreen(
                viewModel = viewModel,
                onSignOut = {
                    viewModel.signOut()
                    controller.navigate(LoginScreenRoute) {
                        popUpTo(MessagesScreenRoute) { inclusive = true }
                    }
                },
                onLaunchSetupOnboarding = {
                    controller.navigate(SetupOnboardingRoute)
                })
        }

        // setup webhook onboarding
        SetupWebhookNavigation(
            controller = controller,
            onClose = { controller.popBackStack() })
    }
}

