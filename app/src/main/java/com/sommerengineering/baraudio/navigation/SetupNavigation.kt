package com.sommerengineering.baraudio.navigation

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.rememberNavController
import com.sommerengineering.baraudio.onboarding.OnboardingScreen
import com.sommerengineering.baraudio.onboarding.WebhookSetup
import com.sommerengineering.baraudio.onboarding.WebhookSetup.CopyWebhook
import com.sommerengineering.baraudio.onboarding.WebhookSetup.PasteWebhook
import com.sommerengineering.baraudio.onboarding.WebhookSetup.SignalArmed
import com.sommerengineering.baraudio.uitls.CopyWebhookScreenRoute
import com.sommerengineering.baraudio.uitls.PasteWebhookScreenRoute
import com.sommerengineering.baraudio.uitls.SignalArmedScreenRoute
import com.sommerengineering.baraudio.onboarding.OnboardingMode.WebhookSetup

@Composable
fun SetupNavigation(
    onClose: () -> Unit) {

    val controller = rememberNavController()

    NavHost(
        navController = controller,
        startDestination = CopyWebhookScreenRoute,
        modifier = Modifier.fillMaxSize()) {

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