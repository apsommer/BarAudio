package com.sommerengineering.baraudio.onboarding

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextAlign
import com.sommerengineering.baraudio.onboarding.OnboardingMode.AppOnboarding
import com.sommerengineering.baraudio.onboarding.OnboardingMode.WebhookSetup
import com.sommerengineering.baraudio.uitls.allowNotificationsMessage
import com.sommerengineering.baraudio.uitls.appOnboardingTtsTitle
import com.sommerengineering.baraudio.uitls.appOnboardingWebhookTitle
import com.sommerengineering.baraudio.uitls.setupOnboardingCopyTitle
import com.sommerengineering.baraudio.uitls.setupOnboardingPasteTitle
import com.sommerengineering.baraudio.uitls.setupOnboardingSignalTitle

@Composable
fun ColumnScope.OnboardingText(
    onboardingMode: OnboardingMode,
    pageNumber: Int) {

    val annotatedString = buildAnnotatedString { when (onboardingMode) {

        AppOnboarding -> { when (pageNumber) {
            0 -> { append(appOnboardingTtsTitle) }
            1 -> { append(allowNotificationsMessage) }
            2 -> { append(appOnboardingWebhookTitle) }
        }}

        WebhookSetup -> { when (pageNumber) {
            0 -> append(setupOnboardingCopyTitle)
            1 -> append(setupOnboardingPasteTitle)
            2 -> append(setupOnboardingSignalTitle)
        }}
    }}

    Column(
        modifier = Modifier.weight(1f),
        verticalArrangement = Arrangement.Center) {

        Text(
            text = annotatedString,
            textAlign = TextAlign.Center,
            style = MaterialTheme.typography.titleLarge)
    }
}