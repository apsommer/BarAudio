package com.sommerengineering.baraudio.onboarding.app

import com.sommerengineering.baraudio.source.Message



fun onboardingMessages(): List<Message> {

    // stagger the timestamp in each message by linear amount
    val now = System.currentTimeMillis()
    val staggerMillis = 1000L

    return listOf(
        Message(
            timestamp = (now - staggerMillis).toString(),
            message = "Macro Supportive • Treasuries +0.36% • Dollar falling",
            stream = "GC",
            source = null),
        Message(
            timestamp = (now - 2 * staggerMillis).toString(),
            message = "Acceptance • Upside holding • 7125.25",
            stream = "ES",
            source = null),
        Message(
            timestamp = (now - 3 * staggerMillis).toString(),
            message = "Cascade • Bullish short liquidation • 157 points",
            stream = "NQ",
            source = null),
        Message(
            timestamp = (now - 4 * staggerMillis).toString(),
            message = "Impulse • Bearish momentum • -0.48%",
            stream = "BTC",
            source = null),
        Message(
            timestamp = (now - 5 * staggerMillis).toString(),
            message = "Repricing • Yields rising • +0.12%",
            stream = "ZN",
            source = null))
}