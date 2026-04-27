package com.sommerengineering.signalvoice.onboarding.webhook

enum class VerificationState {
    WAITING,
    RECEIVED
}

data class VerificationUiState(
    val state: VerificationState,
    val message: String? = null
)
