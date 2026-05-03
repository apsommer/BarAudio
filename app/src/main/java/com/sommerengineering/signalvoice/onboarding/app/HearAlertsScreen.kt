package com.sommerengineering.signalvoice.onboarding.app

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.sommerengineering.signalvoice.onboarding.OnboardingScreen
import com.sommerengineering.signalvoice.uitls.nextText
import com.sommerengineering.signalvoice.uitls.onboardingHearAlertsSubTitle
import com.sommerengineering.signalvoice.uitls.onboardingHearAlertsTitle

@Composable
fun HearAlertsScreen(
    onNextClick: () -> Unit
) {

    val message = onboardingMessage()

    // fade in subtly
    var isVisible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { isVisible = true }

    OnboardingScreen(
        title = onboardingHearAlertsTitle,
        subTitle = onboardingHearAlertsSubTitle,
        pageNumber = 0,
        buttonText = nextText,
        onNextClick = onNextClick
    ) {

        AnimatedVisibility(
            visible = isVisible,
            enter = fadeIn(tween(1000))
        ) {
            OnboardingMessageUi(
                message = message,
                isExpanded = true
            )
        }
    }
}