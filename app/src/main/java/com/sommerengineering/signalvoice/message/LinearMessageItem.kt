package com.sommerengineering.signalvoice.message

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.combinedClickable
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import com.sommerengineering.signalvoice.source.MessageOrigin
import com.sommerengineering.signalvoice.source.OriginIcon
import com.sommerengineering.signalvoice.uitls.dividerThickness
import com.sommerengineering.signalvoice.uitls.messageItemExpansionTimeMillis
import com.sommerengineering.signalvoice.uitls.rowHorizontalPadding
import com.sommerengineering.signalvoice.uitls.rowIconPadding
import com.sommerengineering.signalvoice.uitls.rowVerticalPadding
import com.sommerengineering.signalvoice.uitls.rowHeight

@Composable
fun LinearMessageItem(
    state: MessageItemState,
    isShowDivider: Boolean
) {

    // prepend asset display name for streams
    val displayText =
        if (state.origin is MessageOrigin.BroadcastStream) {
            "${state.origin.displayName} • ${state.text}"
        } else {
            state.text
        }

    Column {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .then(
                    if (state.isExpanded) Modifier.height(IntrinsicSize.Min) // dynamic expanded
                    else Modifier.height(rowHeight)
                ) // fixed collapsed
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
            LinearRail(state.style.primary)

            val modifier = Modifier
                .weight(1f)
                .fillMaxHeight()
                .padding(vertical = rowVerticalPadding)

            // collapsed
            if (!state.isExpanded) {
                LinearCollapsedMessageItem(
                    state = state,
                    displayText = displayText,
                    modifier = modifier
                )

                // expanded, parity with expanded GroupedMessageItem
            } else {
                ExpandedMessageItem(
                    state = state,
                    displayText = displayText,
                    modifier = modifier
                )
            }

            // origin image
            Spacer(Modifier.width(rowIconPadding))
            OriginIcon(state.origin)
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
