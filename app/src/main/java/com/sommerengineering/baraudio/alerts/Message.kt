package com.sommerengineering.baraudio.alerts

import android.content.res.Configuration.UI_MODE_NIGHT_NO
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.unit.dp
import com.sommerengineering.baraudio.theme.AppTheme

data class Message(
    var timestamp: String,
    var message: String
)

fun getAlerts() : List<Message> {
    return listOf(
        Message("66558816355", "Apples and bananas"),
        Message("5599756655", "Carrots and eggplant"),
        Message("774455998888", "Tiramisu and donuts")
    )
}

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
            Text(text = message.timestamp)
            Text(text = message.message)
        }
    }
}

////////////////////////////////////////////////////////////////////////////////////////////////////

@Preview(
    uiMode = UI_MODE_NIGHT_YES,
    name = "dark"
)
@Preview(
    uiMode = UI_MODE_NIGHT_NO,
    name = "light"
)
@Composable
fun PreviewAlertItem() {
    AppTheme {
        MessageItem(getAlerts()[0])
    }
}