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
import com.sommerengineering.baraudio.uitls.allowNotificationsTitle
import com.sommerengineering.baraudio.uitls.onboardingTtsTitle
import com.sommerengineering.baraudio.uitls.onboardingWebhookTitle

@Composable
fun ColumnScope.OnboardingText(
    onboardingMode: OnboardingMode,
    pageNumber: Int) {

    val annotatedString = buildAnnotatedString { when (onboardingMode) {

        AppOnboarding -> { when (pageNumber) {
            0 -> { append(onboardingTtsTitle) }
            1 -> { append(allowNotificationsTitle) }
            2 -> { append(onboardingWebhookTitle) }
        }}

        WebhookSetup -> { when (pageNumber) {
            0 -> append("Copy your webhook URL into TradingView.")
            1 -> append("Create a TradingView alert and paste the webhook URL.")
            2 -> append("We’re now listening. Send a test alert to verify.")
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