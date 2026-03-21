package com.sommerengineering.baraudio.onboarding

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.text.withStyle
import com.sommerengineering.baraudio.onboarding.OnboardingMode.AppOnboarding
import com.sommerengineering.baraudio.onboarding.OnboardingMode.SetupWebhook
import com.sommerengineering.baraudio.uitls.allowNotificationsMessage
import com.sommerengineering.baraudio.uitls.appOnboardingTtsTitle
import com.sommerengineering.baraudio.uitls.appOnboardingWebhookTitle
import com.sommerengineering.baraudio.uitls.setupOnboardingCopyTitle
import com.sommerengineering.baraudio.uitls.setupOnboardingPasteSubtitle
import com.sommerengineering.baraudio.uitls.setupOnboardingPasteTitle
import com.sommerengineering.baraudio.uitls.setupOnboardingSignalTitle

@Composable
fun OnboardingText(
    onboardingMode: OnboardingMode,
    pageNumber: Int,
    modifier: Modifier) {

    val annotatedString = buildAnnotatedString { when (onboardingMode) {

        AppOnboarding -> { when (pageNumber) {
            0 -> { append(appOnboardingTtsTitle) }
            1 -> { append(allowNotificationsMessage) }
            2 -> { append(appOnboardingWebhookTitle) }
        }}

        SetupWebhook -> { when (pageNumber) {
            0 -> append(setupOnboardingCopyTitle)
            1 -> {
                append(setupOnboardingPasteTitle)
                withStyle(style = MaterialTheme.typography.bodyMedium.toSpanStyle()) {
                    append(setupOnboardingPasteSubtitle) }
            }
            2 -> append(setupOnboardingSignalTitle)
        }}
    }}

    Text(
        modifier = modifier,
        text = annotatedString,
        textAlign = TextAlign.Center,
        style = MaterialTheme.typography.titleLarge)
}