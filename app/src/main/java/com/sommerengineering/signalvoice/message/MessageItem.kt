package com.sommerengineering.signalvoice.message

import androidx.compose.animation.animateColorAsState
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import com.sommerengineering.signalvoice.MainViewModel
import com.sommerengineering.signalvoice.messages.FeedMode
import com.sommerengineering.signalvoice.source.Message
import com.sommerengineering.signalvoice.source.resolveMessageOrigin
import com.sommerengineering.signalvoice.uitls.TimestampFormatter
import kotlinx.coroutines.delay

@Composable
fun MessageItem(
    viewModel: MainViewModel,
    message: Message,
    isShowDivider: Boolean
) {

    // extract message attributes
    val timestamp = message.timestamp
    val text = message.message

    // style from origin
    val origin = resolveMessageOrigin(message)
    val style = resolveMessageStyle(origin)

    // detect tap (expand) and long press (speak)
    var isExpanded by remember { mutableStateOf(false) }
    var isLongPress by remember { mutableStateOf(false) }

    // animate background color on click events
    val backgroundColor by animateColorAsState(
        when {
            isExpanded -> MaterialTheme.colorScheme.surfaceContainerHighest
            isLongPress -> MaterialTheme.colorScheme.secondaryContainer
            else -> MaterialTheme.colorScheme.surfaceContainer
        },
        label = "background"
    )

    // update timestamp once per minute
    var beautifulTimestamp by remember { mutableStateOf("") }
    LaunchedEffect(Unit) {
        while (true) {
            beautifulTimestamp = TimestampFormatter.beautifyCompact(timestamp)
            val now = System.currentTimeMillis() // millis since epoch
            val delayMillis = 60_000L - (now % 60_000L) // millis remaining in current minute
            delay(delayMillis) // wait until next minute boundary
        }
    }

    // clear long press animation after delay
    LaunchedEffect(isLongPress) {
        if (!isLongPress) return@LaunchedEffect
        delay(180)
        isLongPress = false
    }

    // ui state
    val state = MessageItemState(
        text = text,
        timestamp = timestamp,
        beautifulTimestamp = beautifulTimestamp,
        origin = origin,
        style = style,
        isExpanded = isExpanded,
        backgroundColor = backgroundColor,
        onClick = { isExpanded = !isExpanded },
        onLongClick = {
            isExpanded = true
            isLongPress = true
            viewModel.speakMessage(message)
        })

    // feed mode
    val feedMode = viewModel.feedMode
    if (feedMode == FeedMode.Linear) {
        LinearMessageItem(
            state = state,
            isShowDivider = isShowDivider
        )
        return
    }
    GroupedMessageItem(
        state = state,
        isShowDivider = isShowDivider
    )
}
