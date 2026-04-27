package com.sommerengineering.signalvoice.onboarding.webhook

import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import com.sommerengineering.signalvoice.MainViewModel
import com.sommerengineering.signalvoice.onboarding.OnboardingScreen
import com.sommerengineering.signalvoice.uitls.doneText
import com.sommerengineering.signalvoice.uitls.onboardingListeningSubTitle
import com.sommerengineering.signalvoice.uitls.onboardingListeningTitle

@Composable
fun SignalVerificationScreen(
    viewModel: MainViewModel,
    onClose: () -> Unit
) {

    val verificationUiState by viewModel.verificationUiState.collectAsState()

    LaunchedEffect(Unit) { viewModel.setVerificationStartTime() }

    OnboardingScreen(
        title = onboardingListeningTitle,
        subTitle = onboardingListeningSubTitle,
        pageNumber = 2,
        buttonText = doneText,
        onNextClick = onClose,
        isNextEnabled = verificationUiState.state == VerificationState.RECEIVED,
        onCloseClick = onClose
    ) {

        VerificationContent(verificationUiState)
    }
}