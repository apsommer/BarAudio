package com.sommerengineering.baraudio.onboarding

import com.sommerengineering.baraudio.source.Message

fun onboardingMessages(): List<Message> {

    // stagger the timestamp in each message by linear amount
    val now = System.currentTimeMillis()
    val staggerMillis = 1000L

    return listOf(
        Message(
            timestamp = (now - staggerMillis).toString(),
            message = "Cascade • Stop cascade • Velocity spike",
            stream = "GC",
            source = null),
        Message(
            timestamp = (now - 2 * staggerMillis).toString(),
            message = "Exhaustion • Momentum fading",
            stream = "ES",
            source = null),
        Message(
            timestamp = (now - 3 * staggerMillis).toString(),
            message = "Continuation • Range expansion",
            stream = "NQ",
            source = null),
        Message(
            timestamp = (now - 4 * staggerMillis).toString(),
            message = "Expansion • Volatility building",
            stream = "BTC",
            source = null),
        Message(
            timestamp = (now - 5 * staggerMillis).toString(),
            message = "Compression • Range tightening",
            stream = "ZN",
            source = null))
}