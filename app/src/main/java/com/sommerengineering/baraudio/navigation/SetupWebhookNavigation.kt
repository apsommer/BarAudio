package com.sommerengineering.baraudio.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.sommerengineering.baraudio.onboarding.OnboardingMode.WebhookSetup
import com.sommerengineering.baraudio.onboarding.OnboardingScreen
import com.sommerengineering.baraudio.uitls.SetupOnboardingCopyWebhookRoute
import com.sommerengineering.baraudio.uitls.SetupOnboardingPasteWebhookRoute
import com.sommerengineering.baraudio.uitls.SetupOnboardingRoute
import com.sommerengineering.baraudio.uitls.SetupOnboardingSignalArmedRoute

fun NavGraphBuilder.SetupWebhookNavigation(
    controller: NavHostController,
    onClose: () -> Unit) {

    navigation(
        route = SetupOnboardingRoute,
        startDestination = SetupOnboardingCopyWebhookRoute) {

        // copy webhook
        composable(SetupOnboardingCopyWebhookRoute) {
            OnboardingScreen(
                onboardingMode = WebhookSetup,
                pageNumber = 0,
                onNextClick = {
                    controller.navigate(SetupOnboardingPasteWebhookRoute) {
                        popUpTo(SetupOnboardingCopyWebhookRoute) { inclusive = true }
                    }
                })
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