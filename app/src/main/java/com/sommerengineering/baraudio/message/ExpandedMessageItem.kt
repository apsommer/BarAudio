package com.sommerengineering.baraudio.message

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sommerengineering.baraudio.source.MessageOrigin
import com.sommerengineering.baraudio.theme.timestampTextStyle
import com.sommerengineering.baraudio.uitls.TimestampFormatter
import com.sommerengineering.baraudio.uitls.rowVerticalPadding

@Composable
fun ExpandedMessageItem(
    state: MessageItemState,
    displayText: String,
    modifier: Modifier = Modifier) {

    Column(modifier) {

        // message
        Text(
            text = displayText,
            style = MaterialTheme.typography.bodyMedium)
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
}