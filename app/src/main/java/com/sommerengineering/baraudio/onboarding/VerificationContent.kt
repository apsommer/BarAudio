package com.sommerengineering.baraudio.onboarding

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.scaleIn
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.sommerengineering.baraudio.onboarding.VerificationState.RECEIVED
import com.sommerengineering.baraudio.onboarding.VerificationState.WAITING
import com.sommerengineering.baraudio.uitls.edgePadding

@Composable
fun VerificationContent(
    verificationUiState: VerificationUiState) {

    AnimatedContent(
        targetState = verificationUiState,
        transitionSpec =  {
            fadeIn(tween(220)) +
            scaleIn(initialScale = 0.95f) togetherWith fadeOut(tween(120)) }) {

        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(edgePadding),
            horizontalAlignment = Alignment.CenterHorizontally,
            verticalArrangement = Arrangement.Center) {

            when (it.state) {
                WAITING -> {
                    Text("Waiting for your first signal...")
                }

                RECEIVED -> {
                    Text("Signal received successfully.")
                }
            }
        }
    }
}