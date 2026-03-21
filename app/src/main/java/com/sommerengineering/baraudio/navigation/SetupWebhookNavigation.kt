package com.sommerengineering.baraudio.navigation

import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.platform.LocalContext
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.sommerengineering.baraudio.MainViewModel
import com.sommerengineering.baraudio.onboarding.OnboardingMode.SetupWebhook
import com.sommerengineering.baraudio.onboarding.OnboardingScreen
import com.sommerengineering.baraudio.onboarding.webhook.VerificationContent
import com.sommerengineering.baraudio.onboarding.webhook.VerificationState
import com.sommerengineering.baraudio.onboarding.webhook.WebhookUrlCard
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
            val onClick = {
                viewModel.copyWebhook(context)
                controller.navigate(SetupOnboardingPasteWebhookRoute) {
                popUpTo(SetupOnboardingCopyWebhookRoute) { inclusive = true }
            }}
            OnboardingScreen(
                onboardingMode = SetupWebhook,
                pageNumber = 0,
                onNextClick = onClick) {
                WebhookUrlCard(
                    viewModel = viewModel,
                    onClick = onClick)
            }
        }

        // paste webhook
        composable(SetupOnboardingPasteWebhookRoute) {
            OnboardingScreen(
                onboardingMode = SetupWebhook,
                pageNumber = 1,
                onNextClick = {
                    controller.navigate(SetupOnboardingSignalArmedRoute) {
                        popUpTo(SetupOnboardingPasteWebhookRoute) { inclusive = true }
                    }})
        }

        // signal armed (setup complete)
        composable(SetupOnboardingSignalArmedRoute) {
            val verificationUiState by viewModel.verificationUiState.collectAsState()
            LaunchedEffect(Unit) { viewModel.setVerificationStartTime() }
            OnboardingScreen(
                onboardingMode = SetupWebhook,
                pageNumber = 2,
                onNextClick = onClose,
                isNextEnabled = verificationUiState.state == VerificationState.RECEIVED) {
                VerificationContent(verificationUiState)
            }
        }
    }
}