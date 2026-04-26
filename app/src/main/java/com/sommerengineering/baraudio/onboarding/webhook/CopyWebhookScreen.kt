package com.sommerengineering.baraudio.onboarding.webhook

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sommerengineering.baraudio.R
import com.sommerengineering.baraudio.onboarding.NodeCard
import com.sommerengineering.baraudio.onboarding.NodeConnector
import com.sommerengineering.baraudio.onboarding.OnboardingScreen
import com.sommerengineering.baraudio.uitls.copyText
import com.sommerengineering.baraudio.uitls.onboardingCopyWebhookSubtitle
import com.sommerengineering.baraudio.uitls.onboardingCopyWebhookTitle

@Composable
fun CopyWebhookScreen(
    webhookUrl: String,
    onNextClick: () -> Unit
) {

    val connectorLength = 80.dp
    val connectorWidth = 2.dp

    OnboardingScreen(
        title = onboardingCopyWebhookTitle,
        subTitle = onboardingCopyWebhookSubtitle,
        pageNumber = 0,
        buttonText = copyText,
        onNextClick = onNextClick
    ) {

        Column(
            modifier = Modifier.fillMaxWidth(),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {

            NodeCard(
                title = "Webhook",
                iconRes = R.drawable.webhook
            )

            NodeConnector(connectorLength, connectorWidth)

            NodeCard(
                title = "SignalVoice",
                iconRes = R.drawable.appbar_compact
            )

            NodeConnector(connectorLength, connectorWidth)

            WebhookUrlCard(
                webhookUrl = webhookUrl,
                onClick = onNextClick
            )
        }
    }
}