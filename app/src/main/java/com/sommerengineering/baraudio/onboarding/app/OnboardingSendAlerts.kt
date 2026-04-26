package com.sommerengineering.baraudio.onboarding.app

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sommerengineering.baraudio.R
import com.sommerengineering.baraudio.message.LinearMessageItem

@Composable
fun OnboardingSendAlerts(modifier: Modifier = Modifier) {

    // define message and state (same as first onboarding screen for consistency)
    val message = onboardingMessage()
    val state = getOnboardingMessageState(
        message = message,
        isExpanded = false
    )

    val connectorLength = 80.dp
    val connectorWidth = 2.dp

    AppOnboardingScreen {

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

            LinearMessageItem(
                state = state,
                isShowDivider = false
            )
        }
    }

}