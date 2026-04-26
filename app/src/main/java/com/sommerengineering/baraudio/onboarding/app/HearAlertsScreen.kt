package com.sommerengineering.baraudio.onboarding.app

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.sommerengineering.baraudio.message.LinearMessageItem
import com.sommerengineering.baraudio.onboarding.OnboardingScreen
import com.sommerengineering.baraudio.uitls.nextText
import com.sommerengineering.baraudio.uitls.onboardingHearAlertsSubTitle
import com.sommerengineering.baraudio.uitls.onboardingHearAlertsTitle

@Composable
fun HearAlertsScreen(
    onNextClick: () -> Unit
) {

    val message = onboardingMessage()
    val state = getOnboardingMessageState(
        message = message, isExpanded = false
    )

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
            LinearMessageItem(
                state = state,
                isShowDivider = false
            )
        }
    }
}