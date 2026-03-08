package com.sommerengineering.baraudio.messages

import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.sommerengineering.baraudio.MainViewModel
import com.sommerengineering.baraudio.source.Message
import com.sommerengineering.baraudio.source.MessageOrigin
import com.sommerengineering.baraudio.source.resolveMessageOrigin
import com.sommerengineering.baraudio.uitls.TimestampFormatter
import com.sommerengineering.baraudio.uitls.rowAccentWidth
import kotlinx.coroutines.delay

@Composable
fun MessageItem(
    viewModel: MainViewModel,
    message: Message,
    isShowDivider: Boolean,
//    modifier: Modifier
) {

    // extract message attributes
    val timestamp = message.timestamp
    val text = message.message
    var beautifulTimestamp by remember { mutableStateOf("") }

    // style from origin
    val isDarkMode = viewModel.isDarkMode
    val origin = resolveMessageOrigin(message)
    val style = resolveMessageStyle(origin, isDarkMode)

    // detect tap (expand) and long press (speak)
    var isExpanded by remember { mutableStateOf(false) }
    var isLongPress by remember { mutableStateOf(false) }

    // animate background color on click events
    val backgroundColor by animateColorAsState(when {
        isExpanded -> MaterialTheme.colorScheme.surfaceContainerHighest
        isLongPress -> MaterialTheme.colorScheme.secondaryContainer
        else -> MaterialTheme.colorScheme.surfaceContainer },
        label = "background")

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

    // ui state
    val messageItemState = MessageItemState(
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
        }
    )

    val feedMode = viewModel.feedMode

    if (feedMode == FeedMode.Linear) {

        LinearMessageItem(
            state = messageItemState,
            isShowDivider = isShowDivider
        )
        return
    }

    GroupedMessageItem(
        state = messageItemState,
        isShowDivider = isShowDivider
    )
}

@Composable
fun Rail(color: Color) {

    Box(
        Modifier
            .width(rowAccentWidth)
            .fillMaxHeight()
    ) {

        Box(
            Modifier
                .align(Alignment.Center)
                .width(rowAccentWidth / 2)
                .fillMaxHeight()
                .background(color.copy(alpha = 0.6f))
        )
    }
}