package com.sommerengineering.baraudio.onboarding.webhook

import androidx.compose.runtime.Composable
import com.sommerengineering.baraudio.onboarding.OnboardingScreen
import com.sommerengineering.baraudio.uitls.copyText
import com.sommerengineering.baraudio.uitls.onboardingCopyWebhookSubtitle
import com.sommerengineering.baraudio.uitls.onboardingCopyWebhookTitle

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