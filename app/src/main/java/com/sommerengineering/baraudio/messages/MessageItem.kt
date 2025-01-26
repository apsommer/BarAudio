package com.sommerengineering.baraudio.messages

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.sommerengineering.baraudio.MainViewModel
import com.sommerengineering.baraudio.beautifyTimestamp
import com.sommerengineering.baraudio.edgePadding
import com.sommerengineering.baraudio.settingsIconSize

@Composable
fun MessageItem(
    viewModel: MainViewModel,
    isRecent: Boolean,
    modifier: Modifier,
    message: Message) {

    Surface(
        modifier = modifier
            .padding(
                horizontal = 8.dp,
                vertical = 4.dp),
        color = Color.Transparent) {

        Column {

            // container
            Row(
                modifier = Modifier
                    .clip(
                        RoundedCornerShape(8.dp))
                    .border(
                        BorderStroke(
                            width = 1.dp,
                            color =
                                if (isRecent) MaterialTheme.colorScheme.outlineVariant
                                else MaterialTheme.colorScheme.outlineVariant),
                        shape = RoundedCornerShape(8.dp))
                    .background(
                        color =
                            if (isRecent) MaterialTheme.colorScheme.surfaceBright
                            else MaterialTheme.colorScheme.surfaceContainer)
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
                        .padding(edgePadding))

                // origin
                viewModel
                    .getOriginImageId(message.origin)?.let {
                    Image(
                        modifier = Modifier
                            .size(settingsIconSize),
                        painter = painterResource(it),
                        contentDescription = null)
                }
            }
        }
    }
}
