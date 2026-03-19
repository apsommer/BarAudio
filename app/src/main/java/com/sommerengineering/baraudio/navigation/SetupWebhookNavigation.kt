package com.sommerengineering.baraudio.navigation

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.input.pointer.motionEventSpy
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.navigation.NavGraphBuilder
import androidx.navigation.NavHostController
import androidx.navigation.compose.composable
import androidx.navigation.compose.navigation
import com.sommerengineering.baraudio.MainViewModel
import com.sommerengineering.baraudio.onboarding.OnboardingMode.SetupWebhook
import com.sommerengineering.baraudio.onboarding.OnboardingScreen
import com.sommerengineering.baraudio.onboarding.VerificationState.RECEIVED
import com.sommerengineering.baraudio.onboarding.VerificationState.WAITING
import com.sommerengineering.baraudio.uitls.SetupOnboardingCopyWebhookRoute
import com.sommerengineering.baraudio.uitls.SetupOnboardingPasteWebhookRoute
import com.sommerengineering.baraudio.uitls.SetupOnboardingRoute
import com.sommerengineering.baraudio.uitls.SetupOnboardingSignalArmedRoute
import com.sommerengineering.baraudio.uitls.edgePadding

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
                onboardingMode = SetupWebhook,
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
                onboardingMode = SetupWebhook,
                pageNumber = 1,
                onNextClick = {
                    controller.navigate(SetupOnboardingSignalArmedRoute) {
                        popUpTo(SetupOnboardingPasteWebhookRoute) { inclusive = true }
                    }})
        }

        // signal armed (setup complete)
        composable(SetupOnboardingSignalArmedRoute) {
            val verificationState by viewModel.verificationState.collectAsState()
            LaunchedEffect(Unit) { viewModel.setVerificationStartTime() }
            OnboardingScreen(
                onboardingMode = SetupWebhook,
                pageNumber = 2,
                onNextClick = onClose,
                isNextEnabled = verificationState == RECEIVED) {
                Column(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(edgePadding),
                    horizontalAlignment = Alignment.CenterHorizontally) {
                    when (verificationState) {
                        WAITING -> { Text("Waiting for your first signal...") }
                        RECEIVED -> { Text("Signal received successfully.") }
                    }
                }
            }
        }
    }
}