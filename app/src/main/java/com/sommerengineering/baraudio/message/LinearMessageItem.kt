package com.sommerengineering.baraudio.message

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
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.sommerengineering.baraudio.source.MessageOrigin
import com.sommerengineering.baraudio.uitls.TimestampFormatter
import com.sommerengineering.baraudio.uitls.assetIconSize
import com.sommerengineering.baraudio.uitls.dividerThickness
import com.sommerengineering.baraudio.uitls.messageItemExpansionTimeMillis
import com.sommerengineering.baraudio.uitls.rowAccentWidth
import com.sommerengineering.baraudio.uitls.rowHorizontalPadding
import com.sommerengineering.baraudio.uitls.rowIconPadding
import com.sommerengineering.baraudio.uitls.rowMinHeight
import com.sommerengineering.baraudio.uitls.rowVerticalPadding

@Composable
fun LinearMessageItem(
    state: MessageItemState,
    isShowDivider: Boolean) {

    // prepend asset display name for streams
    val displayText =
        if (state.origin is MessageOrigin.BroadcastStream) { "${state.origin.displayName}: ${state.text}" }
        else { state.text }

    Column {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min) // required for collapse/expand animation
                .heightIn(min = rowMinHeight)
                .combinedClickable(
                    onClick = state.onClick,
                    onLongClick = state.onLongClick)
                .animateContentSize(tween(messageItemExpansionTimeMillis))
                .background(state.backgroundColor)
                .padding(horizontal = rowHorizontalPadding),
            verticalAlignment = Alignment.CenterVertically) {

            // accent rail
            LinearRail(state.style.primary)

            // todo isolate this expanded state and extract
            //  common to both Linear/GroupedMessageItem

            // message, timestamp
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(vertical = rowVerticalPadding),
                horizontalAlignment = Alignment.Start) {
                Text(
                    text = displayText,
                    style = MaterialTheme.typography.titleMedium,
                    maxLines = if (state.isExpanded) Int.MAX_VALUE else 1,
                    overflow = TextOverflow.Ellipsis)
                Spacer(Modifier.height(4.dp))
                Text(
                    text = state.beautifulTimestamp,
                    style = MaterialTheme.typography.bodyMedium)

                // expanded, full timestamp
                if (state.isExpanded) {
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = TimestampFormatter.beautifyFull(state.timestamp),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.outline)
                }
            }

            // origin image
            Spacer(Modifier.width(rowIconPadding))
            Icon(
                painter = painterResource(state.style.iconRes),
                contentDescription = null,
                tint = if (state.style.isIconTinted) state.style.primary else Color.Unspecified,
                modifier = Modifier
                    .padding(vertical = rowVerticalPadding)
                    .size(assetIconSize))
        }

        // divider between rows
        if (isShowDivider) {
            HorizontalDivider(
                thickness = dividerThickness,
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 1f),
                modifier = Modifier.fillMaxWidth())
        }
    }
}
