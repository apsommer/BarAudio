package com.sommerengineering.baraudio.message

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.height
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import com.sommerengineering.baraudio.theme.timestampTextStyle

@Composable
fun LinearCollapsedMessageItem(
    state: MessageItemState,
    displayText: String,
    modifier: Modifier = Modifier) {

    Column(modifier) {

        // message
        Text(
            text = buildStyledMessage(
                displayText = displayText,
                state = state),
            style = MaterialTheme.typography.bodyMedium,
            maxLines = 1,
            overflow = TextOverflow.Ellipsis)
        Spacer(Modifier.height(4.dp))

        // compact timestamp
        Text(
            text = state.beautifulTimestamp,
            style = timestampTextStyle,
            color = MaterialTheme.colorScheme.onSurface.copy(0.6f))
    }
}