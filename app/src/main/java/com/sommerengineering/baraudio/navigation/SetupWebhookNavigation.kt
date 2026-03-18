package com.sommerengineering.baraudio.navigation

import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.sommerengineering.baraudio.onboarding.OnboardingMode.WebhookSetup
import com.sommerengineering.baraudio.onboarding.OnboardingScreen
import com.sommerengineering.baraudio.uitls.CopyWebhookScreenRoute
import com.sommerengineering.baraudio.uitls.PasteWebhookScreenRoute
import com.sommerengineering.baraudio.uitls.SetupOnboardingRoute
import com.sommerengineering.baraudio.uitls.SignalArmedScreenRoute

fun NavGraphBuilder.SetupWebhookNavigation(
    controller: NavHostController,
    onClose: () -> Unit) {

    navigation(
        route = SetupOnboardingRoute,
        startDestination = CopyWebhookScreenRoute) {

        // copy webhook
        composable(CopyWebhookScreenRoute) {
            OnboardingScreen(
                onboardingMode = WebhookSetup,
                pageNumber = 0,
                onNextClick = {
                    controller.navigate(PasteWebhookScreenRoute) {
                        popUpTo(CopyWebhookScreenRoute) { inclusive = true }
                    }
                })
        }

        // paste webhook
        composable(PasteWebhookScreenRoute) {
            OnboardingScreen(
                onboardingMode = WebhookSetup,
                pageNumber = 1,
                onNextClick = {
                    controller.navigate(SignalArmedScreenRoute) {
                        popUpTo(PasteWebhookScreenRoute) { inclusive = true }
                    }})
        }

        // signal armed (setup complete)
        composable(SignalArmedScreenRoute) {
            OnboardingScreen(
                onboardingMode = WebhookSetup,
                pageNumber = 2,
                onNextClick = onClose)
        }
    }
}