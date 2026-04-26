package com.sommerengineering.baraudio.onboarding.app

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sommerengineering.baraudio.R
import com.sommerengineering.baraudio.message.LinearMessageItem
import com.sommerengineering.baraudio.onboarding.BackgroundGlowContainer
import com.sommerengineering.baraudio.onboarding.NodeCard
import com.sommerengineering.baraudio.onboarding.NodeConnector
import com.sommerengineering.baraudio.onboarding.OnboardingScreen
import com.sommerengineering.baraudio.uitls.nextText
import com.sommerengineering.baraudio.uitls.onboardingSendAlertTitle
import com.sommerengineering.baraudio.uitls.onboardingSendAlertsSubtitle

@Composable
fun SendAlertsScreen(
    onNextClick: () -> Unit
) {

    // define message and state (same as first onboarding screen for consistency)
    val message = onboardingMessage()
    val state = getOnboardingMessageState(
        message = message,
        isExpanded = false
    )

    val connectorLength = 80.dp
    val connectorWidth = 2.dp

    OnboardingScreen(
        title = onboardingSendAlertTitle,
        subTitle = onboardingSendAlertsSubtitle,
        pageNumber = 2,
        buttonText = nextText,
        onNextClick = onNextClick
    ) {

        BackgroundGlowContainer {

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
}