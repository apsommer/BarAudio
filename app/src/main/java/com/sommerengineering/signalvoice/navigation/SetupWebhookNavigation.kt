package com.sommerengineering.signalvoice.navigation

import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.sommerengineering.signalvoice.MainViewModel
import com.sommerengineering.signalvoice.onboarding.webhook.CopyWebhookScreen
import com.sommerengineering.signalvoice.onboarding.webhook.PasteWebhookScreen
import com.sommerengineering.signalvoice.onboarding.webhook.SignalVerificationScreen
import com.sommerengineering.signalvoice.uitls.SetupOnboardingCopyWebhookRoute
import com.sommerengineering.signalvoice.uitls.SetupOnboardingPasteWebhookRoute
import com.sommerengineering.signalvoice.uitls.SetupOnboardingRoute
import com.sommerengineering.signalvoice.uitls.SetupOnboardingSignalArmedRoute

fun NavGraphBuilder.SetupWebhookNavigation(
    controller: NavHostController,
    viewModel: MainViewModel,
    onClose: () -> Unit
) {

    navigation(
        route = SetupOnboardingRoute,
        startDestination = SetupOnboardingCopyWebhookRoute
    ) {

        // copy webhook
        composable(SetupOnboardingCopyWebhookRoute) {

            val webhookUrl = viewModel.webhookUrl
            val context = LocalContext.current
            val onNextClick = {
                viewModel.copyWebhook(context)
                controller.navigate(SetupOnboardingPasteWebhookRoute)
            }

            CopyWebhookScreen(
                webhookUrl = webhookUrl,
                onNextClick = onNextClick
            )
        }

        // paste webhook
        composable(SetupOnboardingPasteWebhookRoute) {

            val onNextClick = {
                controller.navigate(SetupOnboardingSignalArmedRoute)
            }

            PasteWebhookScreen(
                onNextClick = onNextClick
            )
        }

        // signal armed (setup complete)
        composable(SetupOnboardingSignalArmedRoute) {

            val onClose: () -> Unit = {
                controller.popBackStack(
                    route = SetupOnboardingRoute,
                    inclusive = true
                )
            }
            SignalVerificationScreen(
                viewModel = viewModel,
                onClose = onClose
            )
        }
    }
}