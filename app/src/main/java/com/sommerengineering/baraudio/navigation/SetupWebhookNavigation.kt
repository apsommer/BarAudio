package com.sommerengineering.baraudio.navigation

import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.sommerengineering.baraudio.MainViewModel
import com.sommerengineering.baraudio.R
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
                    }}) {
                Image(
                    modifier = Modifier
                        .fillMaxWidth()
                        .clip(RoundedCornerShape(8.dp))
                        .border(
                            width = 1.dp,
                            color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.15f),
                            shape = RoundedCornerShape(8.dp)),
                    painter = painterResource(R.drawable.screenshot),
                    contentDescription = null)
            }
        }

        // signal armed (setup complete)
        composable(SetupOnboardingSignalArmedRoute) {
            val verificationUiState by viewModel.verificationUiState.collectAsState()
            LaunchedEffect(Unit) { viewModel.setVerificationStartTime() }
            OnboardingScreen(
                onboardingMode = SetupWebhook,
                pageNumber = 2,
                onNextClick = onClose,
                isNextEnabled = verificationUiState.state == VerificationState.RECEIVED,
                onCloseClick = {
                    controller.popBackStack(
                        route = SetupOnboardingRoute,
                        inclusive = true
                    )
                }) {
                VerificationContent(verificationUiState)
            }
        }
    }
}