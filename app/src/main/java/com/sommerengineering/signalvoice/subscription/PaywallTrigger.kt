package com.sommerengineering.signalvoice.subscription

enum class PaywallTrigger {
    LOCKED_MESSAGE_TAP,
    LOCKED_MESSAGE_AUTO,   // optional after N events
    FEATURE_GATE_SUBSCRIBE,
    FEATURE_GATE_CUSTOM_SIGNAL
}