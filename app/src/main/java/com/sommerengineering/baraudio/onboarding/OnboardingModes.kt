package com.sommerengineering.baraudio.onboarding

enum class OnboardingMode {
    AppOnboarding,
    SetupWebhook
}

enum class AppOnboarding {
    TextToSpeech,
    Notifications,
    Webhook
}

enum class WebhookSetup {
    Copy,
    Paste,
    Verification
}

enum class VerificationState {
    WAITING,
    RECEIVED
}