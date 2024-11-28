package com.sommerengineering.baraudio.messages

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.sommerengineering.baraudio.beautifyTimestamp

@Composable
fun MessageItem(
    message: Message) {

    Surface {
        Column {

            Row(
                modifier = Modifier
                    .padding(
                        horizontal = 16.dp,
                        vertical = 12.dp),
                verticalAlignment = Alignment.CenterVertically) {

                Column(
                    modifier = Modifier
                        .fillMaxSize()
                        .weight(1f),
                    horizontalAlignment = Alignment.Start) {

                    // message
                    Text(
                        text = message.message,
                        style = MaterialTheme.typography.titleMedium)

                    Spacer(
                        modifier = Modifier
                            .height(4.dp))

                    // timestamp
                    Text(
                        text = beautifyTimestamp(message.timestamp),
                        style = MaterialTheme.typography.bodyMedium)
                }

                Spacer(
                    modifier = Modifier
                        .padding(24.dp))

                // origin
                message.originImageId?.let {
                    Image(
                        modifier = Modifier
                            .size(24.dp),
                        painter = painterResource(it),
                        contentDescription = null)
                }
            }

            HorizontalDivider()
        }
    }
}
