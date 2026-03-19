package com.sommerengineering.baraudio.onboarding

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.ColumnScope
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

enum class VerificationState {
    WAITING,
    RECEIVED
}

@Composable
fun ColumnScope.SetupWebhookVerificationColumn(
    verificationState: VerificationState) {

    Column(
        verticalArrangement = Arrangement.spacedBy(16.dp)) {
        when (verificationState) {
            VerificationState.WAITING -> { Text("Waiting for your first signal...") }
            VerificationState.RECEIVED -> { Text("Signal received successfully.") }
        }
    }
}

