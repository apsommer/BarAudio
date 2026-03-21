package com.sommerengineering.baraudio.navigation

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.sommerengineering.baraudio.MainViewModel
import com.sommerengineering.baraudio.onboarding.OnboardingMode.SetupWebhook
import com.sommerengineering.baraudio.onboarding.OnboardingScreen
import com.sommerengineering.baraudio.onboarding.verification.VerificationContent
import com.sommerengineering.baraudio.onboarding.verification.VerificationState
import com.sommerengineering.baraudio.onboarding.verification.WebhookUrlCard
import com.sommerengineering.baraudio.theme.monospacedFontFamily
import com.sommerengineering.baraudio.theme.timestampTextStyle
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