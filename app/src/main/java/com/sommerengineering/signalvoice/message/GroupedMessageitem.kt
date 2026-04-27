package com.sommerengineering.signalvoice.message

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
import com.sommerengineering.signalvoice.source.OriginIcon
import com.sommerengineering.signalvoice.theme.timestampTextStyle
import com.sommerengineering.signalvoice.uitls.dividerThickness
import com.sommerengineering.signalvoice.uitls.messageItemExpansionTimeMillis
import com.sommerengineering.signalvoice.uitls.rowHorizontalPadding
import com.sommerengineering.signalvoice.uitls.rowIconPadding
import com.sommerengineering.signalvoice.uitls.rowVerticalPadding

@Composable
fun GroupedMessageItem(
    state: MessageItemState,
    isShowDivider: Boolean
) {

    // omit prepended asset name
    val displayText = state.text

    Column {
        Row(
            modifier = Modifier
                .fillMaxWidth()
                .height(IntrinsicSize.Min) // required for collapse/expand animation
                .combinedClickable(
                    onClick = state.onClick,
                    onLongClick = state.onLongClick
                )
                .animateContentSize(tween(messageItemExpansionTimeMillis))
                .background(state.backgroundColor)
                .padding(horizontal = rowHorizontalPadding),
            verticalAlignment = Alignment.CenterVertically
        ) {

            // accent rail
            GroupedRail(state.style.primary)

            val modifier = Modifier.weight(1f).padding(vertical = rowVerticalPadding)

            // collapsed
            if (!state.isExpanded) {

                Row(
                    modifier = modifier,
                    verticalAlignment = Alignment.CenterVertically
                ) {

                    // message
                    Text(
                        text = buildStyledMessage(
                            displayText = displayText,
                            state = state
                        ),
                        style = MaterialTheme.typography.bodyMedium,
                        maxLines = 1,
                        overflow = TextOverflow.Ellipsis,
                        modifier = Modifier.weight(1f)
                    )

                    // compact timestamp
                    Text(
                        text = state.beautifulTimestamp,
                        style = timestampTextStyle,
                        color = MaterialTheme.colorScheme.onSurface.copy(0.6f),
                        modifier = Modifier.padding(start = rowHorizontalPadding)
                    )
                }
            }

            // expanded, parity with expanded LinearMessageItem
            else {

                ExpandedMessageItem(
                    state = state,
                    displayText = displayText,
                    modifier = modifier
                )

                // origin image
                Spacer(Modifier.width(rowIconPadding))
                OriginIcon(state.origin)
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
