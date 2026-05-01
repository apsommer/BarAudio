package com.sommerengineering.signalvoice.onboarding.webhook

import androidx.compose.runtime.Composable
import com.sommerengineering.signalvoice.onboarding.OnboardingScreen
import com.sommerengineering.signalvoice.uitls.copyText
import com.sommerengineering.signalvoice.uitls.onboardingCopyWebhookSubtitle
import com.sommerengineering.signalvoice.uitls.onboardingCopyWebhookTitle

@Composable
fun CopyWebhookScreen(
    webhookUrl: String,
    onNextClick: () -> Unit
) {
    

    OnboardingScreen(
        title = onboardingCopyWebhookTitle,
        subTitle = onboardingCopyWebhookSubtitle,
        pageNumber = 0,
        buttonText = copyText,
        onNextClick = onNextClick
    ) {

        WebhookUrlCard(
            webhookUrl = webhookUrl,
            onCopyClick = onNextClick
        )
    }
}