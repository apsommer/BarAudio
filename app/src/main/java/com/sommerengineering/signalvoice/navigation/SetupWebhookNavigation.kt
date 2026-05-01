package com.sommerengineering.signalvoice.navigation

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.sommerengineering.signalvoice.MainViewModel
import com.sommerengineering.signalvoice.Session.Authenticated
import com.sommerengineering.signalvoice.onboarding.webhook.CopyWebhookScreen
import com.sommerengineering.signalvoice.onboarding.webhook.PasteWebhookScreen
import com.sommerengineering.signalvoice.onboarding.webhook.SignalVerificationScreen
import com.sommerengineering.signalvoice.uitls.SetupOnboardingCopyWebhookRoute
import com.sommerengineering.signalvoice.uitls.SetupOnboardingPasteWebhookRoute
import com.sommerengineering.signalvoice.uitls.SetupOnboardingRoute
import com.sommerengineering.signalvoice.uitls.SetupOnboardingSignalArmedRoute
import com.sommerengineering.signalvoice.uitls.webhookBaseUrl

fun NavGraphBuilder.SetupWebhookNavigation(
    controller: NavHostController,
    viewModel: MainViewModel,
    onNavigateToLogin: () -> Unit,
    onClose: () -> Unit
) {

    val session = viewModel.session

    navigation(
        route = SetupOnboardingRoute,
        startDestination = SetupOnboardingCopyWebhookRoute
    ) {

        // copy webhook
        composable(SetupOnboardingCopyWebhookRoute) {

            // authenticated, navigate to webhook setup onboarding
            if (session is Authenticated) {

                val context = LocalContext.current
                val webhookUrl = "$webhookBaseUrl${session.uid}"

                CopyWebhookScreen(
                    webhookUrl = webhookUrl,
                    onNextClick = {
                        viewModel.copyWebhook(context, webhookUrl)
                        controller.navigate(SetupOnboardingPasteWebhookRoute)
                    }
                )
                return@composable
            }

            // guest, navigate to login screen
            LaunchedEffect(Unit) { onNavigateToLogin() }
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

            SignalVerificationScreen(
                viewModel = viewModel,
                onClose = onClose
            )
        }
    }
}