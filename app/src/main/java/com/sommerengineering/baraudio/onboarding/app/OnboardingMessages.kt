package com.sommerengineering.baraudio.onboarding.app

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import com.sommerengineering.baraudio.message.LinearCollapsedMessageItem
import com.sommerengineering.baraudio.message.LinearMessageItem
import com.sommerengineering.baraudio.message.MessageItemState
import com.sommerengineering.baraudio.message.MessageItemStyle
import com.sommerengineering.baraudio.message.resolveMessageStyle
import com.sommerengineering.baraudio.source.Message
import com.sommerengineering.baraudio.source.MessageOrigin
import com.sommerengineering.baraudio.source.resolveMessageOrigin
import com.sommerengineering.baraudio.uitls.TimestampFormatter

@Composable
fun AppOnboardingTextToSpeech() {

    val message = onboardingMessages().first()

    val state = getOnboardingMessageState(
        message = message,
        isExpanded = false)

    LinearMessageItem(
        state = state,
        isShowDivider = false)
}

@Composable
fun getOnboardingMessageState(
    message: Message,
    isExpanded: Boolean,
): MessageItemState {

    // parse message
    val text = message.message
    val timestamp = message.timestamp
    val beautifulTimestamp = TimestampFormatter.beautifyCompact(message.timestamp)
    val origin = resolveMessageOrigin(message)
    val style = resolveMessageStyle(origin)

    return MessageItemState(
        text = text,
        timestamp = timestamp,
        beautifulTimestamp = beautifulTimestamp,
        origin = origin,
        style = style,
        isExpanded = isExpanded,
        backgroundColor = MaterialTheme.colorScheme.surfaceContainer, // todo only hardcode parity with real UI
        onClick = { },
        onLongClick = { })
}

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