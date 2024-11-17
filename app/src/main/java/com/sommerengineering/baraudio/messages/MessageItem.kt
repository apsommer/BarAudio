package com.sommerengineering.baraudio.messages

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.sommerengineering.baraudio.beautifyTimestamp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MessageItem(
    message: Message,
    modifier: Modifier = Modifier
) {

    Surface {
        Row (
            modifier = Modifier.padding(12.dp),
            verticalAlignment = Alignment.CenterVertically) {
            Column(
                horizontalAlignment = Alignment.Start,
                modifier = modifier
                    .fillMaxSize()
                    .weight(1f)) {
                Text(
                    text = message.message,
                    style = MaterialTheme.typography.titleMedium)
                Spacer(modifier = Modifier.height(4.dp))
                Text(
                    text = beautifyTimestamp(message.timestamp),
                    style = MaterialTheme.typography.bodyMedium)
            }
            Spacer(modifier = Modifier.padding(8.dp))
            message.originImageId?.let {
                Image(
                    modifier = Modifier.size(24.dp),
                    painter = painterResource(it),
                    contentDescription = null)
            }
        }
    }
}