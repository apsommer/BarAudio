package com.sommerengineering.baraudio.onboarding

enum class OnboardingMode {
    AppOnboarding,
    WebhookSetup
}

enum class AppOnboarding {
    TextToSpeech,
    Notifications,
    Webhook
}

enum class WebhookSetup {
    CopyWebhook,
    PasteWebhook,
    SignalArmed
}