package com.sommerengineering.baraudio.onboarding.webhook

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.sommerengineering.baraudio.MainViewModel
import com.sommerengineering.baraudio.onboarding.OnboardingScreen
import com.sommerengineering.baraudio.onboarding.webhook.verification.VerificationContent
import com.sommerengineering.baraudio.onboarding.webhook.verification.VerificationState
import com.sommerengineering.baraudio.uitls.doneText
import com.sommerengineering.baraudio.uitls.onboardingListeningTitle

@Composable
fun SignalVerificationScreen(
    viewModel: MainViewModel,
    onClose: () -> Unit
) {

    val verificationUiState by viewModel.verificationUiState.collectAsState()

    LaunchedEffect(Unit) { viewModel.setVerificationStartTime() }

    OnboardingScreen(
        title = onboardingListeningTitle,
        pageNumber = 2,
        buttonText = doneText,
        onNextClick = onClose,
        isNextEnabled = verificationUiState.state == VerificationState.RECEIVED,
        onCloseClick = onClose
    ) {

        VerificationContent(verificationUiState)
    }
}