package com.sommerengineering.signalvoice.message

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sommerengineering.signalvoice.theme.timestampTextStyle
import com.sommerengineering.signalvoice.uitls.TimestampFormatter

@Composable
fun ExpandedMessageItem(
    state: MessageItemState,
    displayText: String,
    modifier: Modifier = Modifier
) {

    Column(modifier) {

        // message
        Text(
            text = buildStyledMessage(
                displayText = displayText,
                state = state
            ),
            style = MaterialTheme.typography.bodyMedium
        )
        Spacer(Modifier.height(4.dp))

        // compact timestamp
        Text(
            text = state.beautifulTimestamp,
            style = timestampTextStyle,
            color = MaterialTheme.colorScheme.onSurface.copy(0.6f)
        )
        Spacer(Modifier.height(4.dp))

        // full timestamp
        Text(
            text = TimestampFormatter.beautifyFull(state.timestamp),
            style = timestampTextStyle,
            color = MaterialTheme.colorScheme.onSurface.copy(0.45f)
        )
    }
}