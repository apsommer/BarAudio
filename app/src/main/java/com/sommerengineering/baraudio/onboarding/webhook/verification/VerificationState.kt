package com.sommerengineering.baraudio.onboarding.webhook.verification

enum class VerificationState {
    WAITING,
    RECEIVED
}

data class VerificationUiState(
    val state: VerificationState,
    val message: String? = null
)
