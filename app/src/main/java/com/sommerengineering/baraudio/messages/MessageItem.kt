package com.sommerengineering.baraudio.messages

import androidx.compose.animation.animateColorAsState
import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.heightIn
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.sommerengineering.baraudio.MainViewModel
import com.sommerengineering.baraudio.source.Message
import com.sommerengineering.baraudio.source.MessageOrigin
import com.sommerengineering.baraudio.source.resolveMessageOrigin
import com.sommerengineering.baraudio.uitls.TimestampFormatter
import com.sommerengineering.baraudio.uitls.assetIconSize
import com.sommerengineering.baraudio.uitls.dividerThickness
import com.sommerengineering.baraudio.uitls.messageItemExpansionTimeMillis
import com.sommerengineering.baraudio.uitls.rowAccentWidth
import com.sommerengineering.baraudio.uitls.rowHorizontalPadding
import com.sommerengineering.baraudio.uitls.rowIconPadding
import com.sommerengineering.baraudio.uitls.rowMinHeight
import com.sommerengineering.baraudio.uitls.rowVerticalPadding
import kotlinx.coroutines.delay

@Composable
fun MessageItem(
    viewModel: MainViewModel,
    message: Message,
    isShowDivider: Boolean,
    modifier: Modifier) {

    // extract message attributes
    val timestamp = message.timestamp
    val text = message.message

    // style from origin
    val isDarkMode = viewModel.isDarkMode
    val origin = resolveMessageOrigin(message)
    val style = resolveMessageStyle(origin, isDarkMode)

    // update timestamp once per minute
    var beautifulTimestamp by remember { mutableStateOf("") }
    LaunchedEffect(Unit) {
        while (true) {
            beautifulTimestamp = TimestampFormatter.beautifyCompact(timestamp)
            val now = System.currentTimeMillis() // millis since epoch
            val delayMillis = 60_000L - (now % 60_000L) // millis remaining in current minute
            delay(delayMillis) // wait until next minute boundary
        }}

    // prepend asset display name for streams in linear mode
    val feedMode = viewModel.feedMode
    val displayText =
        if (feedMode == FeedMode.Linear
            && origin is MessageOrigin.BroadcastStream) { "${origin.displayName}: $text" }
        else { text }

    // detect tap (expand) and long press (speak)
    var isExpanded by remember { mutableStateOf(false) }
    var isLongPress by remember { mutableStateOf(false) }

    // animate background color on click events
    val backgroundColor by animateColorAsState(when {
        isExpanded -> MaterialTheme.colorScheme.surfaceContainerHighest
        isLongPress -> MaterialTheme.colorScheme.secondaryContainer
        else -> MaterialTheme.colorScheme.surfaceContainer },
        label = "background")

    // clear long press animation after delay
    LaunchedEffect(isLongPress) {
        if (!isLongPress) return@LaunchedEffect
        delay(180)
        isLongPress = false
    }

    Column {

        Row(
            modifier = modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min) // required for collapse/expand animation
                .heightIn(min = rowMinHeight)
                .combinedClickable(
                    onClick = { isExpanded = !isExpanded },
                    onLongClick = {
                        isExpanded = true
                        isLongPress = true
                        viewModel.speakMessage(message) })
                .animateContentSize(tween(messageItemExpansionTimeMillis))
                .background(backgroundColor)
                .padding(horizontal = rowHorizontalPadding),
            verticalAlignment = Alignment.CenterVertically) {

            // accent bar
            Box(
                Modifier
                    .width(rowAccentWidth)
                    .fillMaxHeight()
            ) {

                if (feedMode == FeedMode.Linear) {

                    Box(
                        Modifier
                            .fillMaxSize()
                            .padding(vertical = rowVerticalPadding)
                            .clip(RoundedCornerShape(3.dp))
                            .background(style.primary)
                    )

                } else {

                    Box(
                        Modifier
                            .align(Alignment.Center)
                            .width(rowAccentWidth / 2)
                            .fillMaxHeight()
                            .background(style.primary.copy(alpha = 0.6f))
                    )
                }
            }
            Spacer(Modifier.width(rowIconPadding))

            // message, timestamp
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(vertical = rowVerticalPadding),
                horizontalAlignment = Alignment.Start) {
                Text(
                    text = displayText,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = if (isExpanded) Int.MAX_VALUE else 1,
                    overflow = TextOverflow.Ellipsis)
                Spacer(Modifier.height(4.dp))
                Text(
                    text = beautifulTimestamp,
                    style = MaterialTheme.typography.bodyMedium)

                // expanded, full timestamp
                if (isExpanded) {
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = TimestampFormatter.beautifyFull(timestamp),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.outline)
                }
            }

            // origin image
            Spacer(Modifier.width(rowIconPadding))
            Icon(
                painter = painterResource(style.iconRes),
                contentDescription = null,
                tint = if (style.isIconTinted) style.primary else Color.Unspecified,
                modifier = Modifier
                    .padding(vertical = rowVerticalPadding)
                    .size(assetIconSize)
                    )
        }

        // divider between rows
        if (isShowDivider) {
            HorizontalDivider(
                thickness = dividerThickness,
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 1f),
                modifier = modifier.fillMaxWidth())
        }
    }
}
