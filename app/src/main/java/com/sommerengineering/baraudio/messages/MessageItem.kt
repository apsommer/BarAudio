package com.sommerengineering.baraudio.messages

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.BoxWithConstraints
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
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
                        24.dp),
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

                    Spacer(modifier = Modifier.height(12.dp))

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