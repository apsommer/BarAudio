package com.sommerengineering.baraudio.messages

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.sommerengineering.baraudio.beautifyTimestamp

data class Message(
    var timestamp: String,
    var message: String
)

@Composable
fun MessageItem(
    message: Message,
    modifier: Modifier = Modifier) {

    Surface {
        Column(
            horizontalAlignment = Alignment.Start,
            modifier = modifier
                .fillMaxSize()
                .padding(12.dp)
        ) {
            Text(
                text = message.message,
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = beautifyTimestamp(message.timestamp),
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}
