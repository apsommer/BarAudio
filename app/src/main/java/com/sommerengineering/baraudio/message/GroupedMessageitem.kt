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
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
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

            // message, timestamp
            Column(
                modifier = Modifier
                    .weight(1f)
                    .padding(vertical = rowVerticalPadding),
                horizontalAlignment = Alignment.Start) {

                Text(
                    text = state.text,
                    modifier = Modifier.weight(1f),
                    maxLines = 1,
                    overflow = TextOverflow.Ellipsis)

                Text(
                    text = state.beautifulTimestamp,
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.outline)

                // expanded, full timestamp
                if (state.isExpanded) {
                    Spacer(Modifier.height(4.dp))
                    Text(
                        text = TimestampFormatter.beautifyFull(state.timestamp),
                        style = MaterialTheme.typography.bodySmall,
                        color = MaterialTheme.colorScheme.outline)
                }
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
