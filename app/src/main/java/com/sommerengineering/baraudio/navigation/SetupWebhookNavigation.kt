package com.sommerengineering.baraudio.navigation

import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.sommerengineering.baraudio.MainViewModel
import com.sommerengineering.baraudio.onboarding.OnboardingMode.WebhookSetup
import com.sommerengineering.baraudio.onboarding.OnboardingScreen
import com.sommerengineering.baraudio.uitls.SetupOnboardingCopyWebhookRoute
import com.sommerengineering.baraudio.uitls.SetupOnboardingPasteWebhookRoute
import com.sommerengineering.baraudio.uitls.SetupOnboardingRoute
import com.sommerengineering.baraudio.uitls.SetupOnboardingSignalArmedRoute

fun NavGraphBuilder.SetupWebhookNavigation(
    controller: NavHostController,
    viewModel: MainViewModel,
    onClose: () -> Unit) {

    navigation(
        route = SetupOnboardingRoute,
        startDestination = SetupOnboardingCopyWebhookRoute) {

        // copy webhook
        composable(SetupOnboardingCopyWebhookRoute) {
            val context = LocalContext.current
            OnboardingScreen(
                onboardingMode = WebhookSetup,
                pageNumber = 0,
                onNextClick = {
                    viewModel.copyWebhook(context)
                    controller.navigate(SetupOnboardingPasteWebhookRoute) {
                        popUpTo(SetupOnboardingCopyWebhookRoute) { inclusive = true }
                    }})
        }

        // paste webhook
        composable(SetupOnboardingPasteWebhookRoute) {
            OnboardingScreen(
                onboardingMode = WebhookSetup,
                pageNumber = 1,
                onNextClick = {
                    controller.navigate(SetupOnboardingSignalArmedRoute) {
                        popUpTo(SetupOnboardingPasteWebhookRoute) { inclusive = true }
                    }})
        }

        // signal armed (setup complete)
        composable(SetupOnboardingSignalArmedRoute) {
            OnboardingScreen(
                onboardingMode = WebhookSetup,
                pageNumber = 2,
                onNextClick = onClose)
        }
    }
}