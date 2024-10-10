package com.sommerengineering.baraudio.alerts

import android.content.res.Configuration.UI_MODE_NIGHT_NO
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import com.sommerengineering.baraudio.login.LoginScreen
import com.sommerengineering.baraudio.theme.AppTheme

class Alert(
    var name: String,
    var sound: Int,
    var voice: String,
    var speed: Float,
    var queueBehavior: QueueBehavior,
    var webhook: String
) {

}

sealed class QueueBehavior {
    object AddToQueue : QueueBehavior()
    object ReplaceQueue : QueueBehavior()
}

fun getAlerts() : List<Alert> {
    return listOf(
        Alert("NQ", 1, "English", 1.0f, QueueBehavior.AddToQueue, "https://webhook.123"),
        Alert("ES", 2, "Spanish", 1.1f, QueueBehavior.ReplaceQueue, "https://webhook.234"),
        Alert("RTY", 3, "Swedish", 1.2f, QueueBehavior.AddToQueue, "https://webhook.345"),
        Alert("FESX", 4, "Catalan", 1.3f, QueueBehavior.ReplaceQueue, "https://webhook.456")
    )
}

@Composable
fun AlertItem(
    alert: Alert,
    modifier: Modifier = Modifier) {

    Surface {
        Column(
            horizontalAlignment = Alignment.Start,
            modifier = modifier.fillMaxSize()
        ) {
            Text(text = alert.name)
            Text(text = alert.sound.toString())
            Text(text = alert.voice)
            Text(text = "queue behavior ...")
            Text(text = alert.webhook)
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
        AlertItem(getAlerts()[0])
    }
}