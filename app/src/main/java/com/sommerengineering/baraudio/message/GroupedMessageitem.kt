package com.sommerengineering.baraudio.message

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sommerengineering.baraudio.messages.EllipsisText
import com.sommerengineering.baraudio.source.OriginIcon
import com.sommerengineering.baraudio.theme.timestampTextStyle
import com.sommerengineering.baraudio.uitls.TimestampFormatter
import com.sommerengineering.baraudio.uitls.dividerThickness
import com.sommerengineering.baraudio.uitls.messageItemExpansionTimeMillis
import com.sommerengineering.baraudio.uitls.rowHorizontalPadding
import com.sommerengineering.baraudio.uitls.rowIconPadding
import com.sommerengineering.baraudio.uitls.rowVerticalPadding

@Composable
fun GroupedMessageItem(
    state: MessageItemState,
    isShowDivider: Boolean) {

    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min) // required for collapse/expand animation
                .combinedClickable(
                    onClick = state.onClick,
                    onLongClick = state.onLongClick)
                .animateContentSize(tween(messageItemExpansionTimeMillis))
                .background(state.backgroundColor)
                .padding(horizontal = rowHorizontalPadding),
            verticalAlignment = Alignment.CenterVertically) {

            // accent rail
            GroupedRail(state.style.primary)

            // collapsed
            if (!state.isExpanded) {

                Row(
                    modifier = Modifier
                        .weight(1f)
                        .padding(vertical = rowVerticalPadding),
                    verticalAlignment = Alignment.CenterVertically) {

                    // message
                    EllipsisText(
                        text = state.text,
                        style = MaterialTheme.typography.titleMedium,
                        modifier = Modifier.weight(1f))

                    // compact timestamp
                    Text(
                        text = state.beautifulTimestamp,
                        style = timestampTextStyle,
                        color = MaterialTheme.colorScheme.onSurface.copy(0.6f),
                        modifier = Modifier.padding(start = rowHorizontalPadding))
                }
            }

            // todo isolate this expanded state and extract
            //  common to both Linear/GroupedMessageItem

            // expanded, parity with expanded LinearMessageItem
            else {

                Column(
                    modifier = Modifier
                        .weight(1f)
                        .padding(vertical = rowVerticalPadding),
                    horizontalAlignment = Alignment.Start) {

                    // message
                    Text(
                        text = state.text,
                        style = MaterialTheme.typography.titleMedium)
                    Spacer(Modifier.height(4.dp))

                    // compact timestamp
                    Text(
                        text = state.beautifulTimestamp,
                        style = timestampTextStyle,
                        color = MaterialTheme.colorScheme.onSurface.copy(0.6f))
                    Spacer(Modifier.height(4.dp))

                    // full timestamp
                    Text(
                        text = TimestampFormatter.beautifyFull(state.timestamp),
                        style = timestampTextStyle,
                        color = MaterialTheme.colorScheme.onSurface.copy(0.45f))
                }

                // origin image
                Spacer(Modifier.width(rowIconPadding))
                OriginIcon(
                    messageOrigin = state.origin,
                    isDarkMode = true)
            }
        }

        // divider between rows
        if (isShowDivider) {
            HorizontalDivider(
                thickness = dividerThickness,
                color = MaterialTheme.colorScheme.outlineVariant.copy(alpha = 1f),
                modifier = Modifier.fillMaxWidth()
            )
        }
    }
}
