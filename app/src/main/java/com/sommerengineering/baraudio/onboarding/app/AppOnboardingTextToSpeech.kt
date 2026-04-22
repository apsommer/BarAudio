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

@Composable
fun AppOnboardingTextToSpeech() {

    val message = onboardingMessage()
    val state = getOnboardingMessageState(
        message = message,
        isExpanded = false)

    // fade in subtly
    var isVisible by remember { mutableStateOf(false) }
    LaunchedEffect(Unit) { isVisible = true }
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