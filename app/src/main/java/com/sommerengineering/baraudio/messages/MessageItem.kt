package com.sommerengineering.baraudio.messages

import android.text.format.DateUtils
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
import androidx.compose.material3.SwipeToDismissBox
import androidx.compose.material3.SwipeToDismissBoxValue.EndToStart
import androidx.compose.material3.SwipeToDismissBoxValue.StartToEnd
import androidx.compose.material3.Text
import androidx.compose.material3.rememberSwipeToDismissBoxState
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.sommerengineering.baraudio.MainViewModel
import com.sommerengineering.baraudio.R
import com.sommerengineering.baraudio.edgePadding
import com.sommerengineering.baraudio.settingsIconSize
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

@Composable
fun MessageItem(
    viewModel: MainViewModel,
    isRecent: Boolean,
    modifier: Modifier,
    message: Message,
    onRemove: () -> Unit) {

    val isDarkMode = viewModel.isDarkMode

    // origin image
    val webhookOriginImageId: Int = when (message.origin) {
        in tradingview -> {
            if (isDarkMode) R.drawable.tradingview_light
            else R.drawable.tradingview_dark
        }
        trendspider -> R.drawable.trendspider
        insomnia -> R.drawable.insomnia
        parsingErrorOrigin -> R.drawable.error
        else -> R.drawable.webhook
    }

    SwipeToDismissBox(
        state = rememberSwipeToDismissBoxState(),
        modifier = Modifier.fillMaxSize(),
        onDismiss = { if (it == StartToEnd || it == EndToStart) { onRemove() } },
        backgroundContent = { }) {

        Surface(
            modifier = modifier
                .padding(
                    horizontal = 8.dp,
                    vertical = 4.dp
                ),
            color = Color.Transparent) {

            Column {

                // container
                Row(
                    modifier = Modifier
                        .clip(RoundedCornerShape(8.dp))
                        .border(
                            BorderStroke(
                                width = 1.dp,
                                color =
                                    if (isRecent) MaterialTheme.colorScheme.outlineVariant
                                    else MaterialTheme.colorScheme.outlineVariant
                            ),
                            shape = RoundedCornerShape(8.dp)
                        )
                        .background(
                            color =
                                if (isRecent) MaterialTheme.colorScheme.surfaceBright
                                else MaterialTheme.colorScheme.surfaceContainer
                        )
                        .padding(
                            horizontal = 16.dp,
                            vertical = 12.dp
                        ),
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
                    Image(
                        modifier = Modifier
                            .size(settingsIconSize),
                        painter = painterResource(webhookOriginImageId),
                        contentDescription = null)
                }
            }
        }
    }
}

// must be top level so firebase messaging can access
fun beautifyTimestamp(
    timestamp: String): String {

    val isToday = DateUtils.isToday(timestamp.toLong())

    val pattern =
        if (isToday) "h:mm:ss a" // 6:27:53 PM
        else "h:mm:ss a • MMMM dd, yyyy" //  6:27:53 PM • October 30, 2024

    return SimpleDateFormat(
        pattern,
        Locale.getDefault())
        .format(Date(timestamp.toLong()))
}
