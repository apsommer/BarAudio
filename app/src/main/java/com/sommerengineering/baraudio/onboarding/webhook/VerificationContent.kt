package com.sommerengineering.baraudio.onboarding.webhook

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp

@Composable
fun VerificationContent(
    verificationUiState: VerificationUiState
) {

    AnimatedContent(
        targetState = verificationUiState,
        transitionSpec = {
            fadeIn(tween(220)) +
                    scaleIn(initialScale = 0.95f) togetherWith fadeOut(tween(120))
        }) { uiState ->

        Column(
            modifier = Modifier.fillMaxSize(),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center
        ) {

            when (uiState.state) {
                VerificationState.WAITING -> {
                    ListeningDots()
                    Spacer(Modifier.size(16.dp))
                    Text(
                        text = "Waiting for your first signal ...",
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodyLarge
                    )
                    Text(
                        text = "This can take up to 30 seconds.",
                        textAlign = TextAlign.Center,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.6f)
                    )
                }

                VerificationState.RECEIVED -> {
                    SuccessCheck()
                    Spacer(Modifier.size(16.dp))
                    Text(
                        text = "Signal received successfully.",
                        textAlign = TextAlign.Center
                    )
                    val message = uiState.message ?: return@Column
                    Spacer(Modifier.size(16.dp))
                    MessageBubble(message)
                }
            }
        }
    }
}