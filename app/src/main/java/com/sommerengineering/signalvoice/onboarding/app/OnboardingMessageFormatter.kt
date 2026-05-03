package com.sommerengineering.signalvoice.onboarding.app

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import com.sommerengineering.signalvoice.message.MessageItemUi
import com.sommerengineering.signalvoice.message.resolveMessageStyle
import com.sommerengineering.signalvoice.source.Message
import com.sommerengineering.signalvoice.source.resolveMessageOrigin
import com.sommerengineering.signalvoice.uitls.TimestampFormatter
import com.sommerengineering.signalvoice.uitls.btcStream
import com.sommerengineering.signalvoice.uitls.esStream
import com.sommerengineering.signalvoice.uitls.gcStream
import com.sommerengineering.signalvoice.uitls.nqStream
import com.sommerengineering.signalvoice.uitls.znStream

fun onboardingMessage(): Message {

    val now = System.currentTimeMillis()
    val message = "Macro Supportive • Treasuries +0.36% • Dollar falling"

    return Message(
        timestamp = now.toString(),
        message = message,
        stream = gcStream,
        source = null
    )
}

fun onboardingMessages(): List<Message> {

    // stagger the timestamp in each message by linear amount
    val now = System.currentTimeMillis()
    val staggerMillis = 60 * 1000L

    return listOf(
        Message(
            timestamp = now.toString(),
            message = "Acceptance • Upside holding • 7125.25",
            stream = esStream,
            source = null
        ),
        Message(
            timestamp = (now - staggerMillis).toString(),
            message = "Cascade • Bullish short liquidation • 157 points",
            stream = nqStream,
            source = null
        ),
        Message(
            timestamp = (now - 2 * staggerMillis).toString(),
            message = "Impulse • Bearish momentum • -0.48%",
            stream = btcStream,
            source = null
        ),
        Message(
            timestamp = (now - 3 * staggerMillis).toString(),
            message = "Repricing • Yields rising • +0.12%",
            stream = znStream,
            source = null
        )
    )
}

@Composable
fun OnboardingMessageUi(
    message: Message,
    isExpanded: Boolean
) {

    // parse message
    val text = message.message
    val timestamp = message.timestamp
    val beautifulTimestamp = TimestampFormatter.beautifyCompact(message.timestamp)
    val origin = resolveMessageOrigin(message)
    val style = resolveMessageStyle(origin)

    MessageItemUi(
        displayText = text,
        beautifulTimestamp = beautifulTimestamp,
        timestamp = timestamp,
        backgroundColor = MaterialTheme.colorScheme.surfaceContainer,
        onClick = { },
        onLongPress = { },
        style = style,
        origin = origin,
        isExpanded = isExpanded,
        isShowDivider = false,
        isLocked = false,
        onLockedClick = { },
        isInteractive = false
    )
}