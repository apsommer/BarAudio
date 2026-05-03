package com.sommerengineering.signalvoice.message

import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import com.sommerengineering.signalvoice.theme.timestampTextStyle
import com.sommerengineering.signalvoice.uitls.rowHorizontalPadding

@Composable
fun CollapsedMessageItem(
    displayText: String,
    beautifulTimestamp: String,
    modifier: Modifier = Modifier
) {

    Row(
        modifier = modifier,
        verticalAlignment = Alignment.CenterVertically
    ) {

        // message
        Text(
            text = buildStyledMessage(displayText),
            style = MaterialTheme.typography.bodyMedium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis,
            modifier = Modifier.weight(1f)
        )

        // compact timestamp
        Text(
            text = beautifulTimestamp,
            style = timestampTextStyle,
            color = MaterialTheme.colorScheme.onSurface.copy(0.6f),
            modifier = Modifier.padding(start = rowHorizontalPadding)
        )
    }
}