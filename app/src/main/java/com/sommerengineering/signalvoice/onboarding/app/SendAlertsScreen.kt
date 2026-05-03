package com.sommerengineering.signalvoice.onboarding.app

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sommerengineering.signalvoice.R
import com.sommerengineering.signalvoice.onboarding.NodeCard
import com.sommerengineering.signalvoice.onboarding.NodeConnector
import com.sommerengineering.signalvoice.onboarding.OnboardingScreen
import com.sommerengineering.signalvoice.uitls.nextText
import com.sommerengineering.signalvoice.uitls.onboardingSendAlertTitle
import com.sommerengineering.signalvoice.uitls.onboardingSendAlertsSubtitle

@Composable
fun SendAlertsScreen(
    onNextClick: () -> Unit
) {

    val message = onboardingMessage()

    val connectorLength = 80.dp
    val connectorWidth = 2.dp

    OnboardingScreen(
        title = onboardingSendAlertTitle,
        subTitle = onboardingSendAlertsSubtitle,
        pageNumber = 2,
        buttonText = nextText,
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

            OnboardingMessageUi(
                message = message,
                isExpanded = false
            )
        }
    }
}