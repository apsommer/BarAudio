package com.sommerengineering.baraudio.navigation

import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.sommerengineering.baraudio.MainViewModel
import com.sommerengineering.baraudio.onboarding.webhook.CopyWebhookScreen
import com.sommerengineering.baraudio.onboarding.webhook.PasteWebhookScreen
import com.sommerengineering.baraudio.onboarding.webhook.SignalVerificationScreen
import com.sommerengineering.baraudio.uitls.SetupOnboardingCopyWebhookRoute
import com.sommerengineering.baraudio.uitls.SetupOnboardingPasteWebhookRoute
import com.sommerengineering.baraudio.uitls.SetupOnboardingRoute
import com.sommerengineering.baraudio.uitls.SetupOnboardingSignalArmedRoute

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
                controller.navigate(SetupOnboardingPasteWebhookRoute) {
                    popUpTo(SetupOnboardingCopyWebhookRoute) { inclusive = true }
                }
            }

            CopyWebhookScreen(
                webhookUrl = webhookUrl,
                onNextClick = onNextClick
            )
        }

        // paste webhook
        composable(SetupOnboardingPasteWebhookRoute) {

            val onNextClick = {
                controller.navigate(SetupOnboardingSignalArmedRoute) {
                    popUpTo(SetupOnboardingPasteWebhookRoute) { inclusive = true }
                }
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