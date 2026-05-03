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
import com.sommerengineering.signalvoice.source.MessageOrigin
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

    // premium locked state
    val isLocked = viewModel.isLocked(message)

    // prepend asset display name for streams in linear mode
    val isLinearStream = viewModel.feedMode == FeedMode.Linear
            && origin is MessageOrigin.BroadcastStream
    val displayText =
        if (isLinearStream) "${origin.displayName} • $text"
        else text

    // detect tap (expand) and long press (speak)
    var beautifulTimestamp by remember { mutableStateOf("") }
    var isExpanded by remember { mutableStateOf(false) }
    var isLongPress by remember { mutableStateOf(false) }

    // animate background color on click events
    val backgroundColor by animateColorAsState(
        when {
            isExpanded -> MaterialTheme.colorScheme.surfaceContainerHighest
            isLongPress -> MaterialTheme.colorScheme.secondaryContainer
            else -> MaterialTheme.colorScheme.surfaceContainer
        }
    )

    // update timestamp once per minute
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

    // click handlers
    val onLockedClick = { viewModel.launchPaywall() }
    val onClick = { isExpanded = !isExpanded }
    val onLongPress: () -> Unit = {
        isExpanded = true
        isLongPress = true
        if (isLocked) onLockedClick()
        else viewModel.speakMessage(message)
    }

    MessageItemUi(
        displayText = displayText,
        beautifulTimestamp = beautifulTimestamp,
        timestamp = timestamp,
        backgroundColor = backgroundColor,
        onClick = onClick,
        onLongPress = onLongPress,
        style = style,
        origin = origin,
        isExpanded = isExpanded,
        isLocked = isLocked,
        onLockedClick = onLockedClick,
        isShowDivider = isShowDivider
    )
}
