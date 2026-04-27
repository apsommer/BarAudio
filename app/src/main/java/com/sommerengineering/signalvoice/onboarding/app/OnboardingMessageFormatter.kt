package com.sommerengineering.signalvoice.onboarding.app

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import com.sommerengineering.signalvoice.message.MessageItemState
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
fun getOnboardingMessageState(
    message: Message,
    isExpanded: Boolean
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