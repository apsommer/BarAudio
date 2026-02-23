package com.sommerengineering.baraudio.messages

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.IntrinsicSize
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.VerticalDivider
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.sommerengineering.baraudio.MainViewModel
import com.sommerengineering.baraudio.R
import com.sommerengineering.baraudio.uitls.TimestampFormatter
import com.sommerengineering.baraudio.uitls.edgePadding
import com.sommerengineering.baraudio.uitls.gcStream
import com.sommerengineering.baraudio.uitls.insomnia
import com.sommerengineering.baraudio.uitls.logMessage
import com.sommerengineering.baraudio.uitls.nqStream
import com.sommerengineering.baraudio.uitls.parsingErrorOrigin
import com.sommerengineering.baraudio.uitls.settingsIconSize
import com.sommerengineering.baraudio.uitls.tradingview
import com.sommerengineering.baraudio.uitls.trendspider

@Composable
fun MessageItem(
    viewModel: MainViewModel,
    isRecent: Boolean,
    modifier: Modifier,
    message: Message) {

    // extract attributes
    val timestamp = TimestampFormatter.beautify(message.timestamp)
    val text = message.message
    val origin = message.origin

    // accent color
    val accentColor = when (origin) {
        nqStream -> Color(0xFF7B61FF)
        gcStream -> Color(0xFFD4AF37)
        else -> MaterialTheme.colorScheme.outlineVariant
    }

    logMessage("origin: $origin")

    Surface(modifier) {

        Row(
            modifier = Modifier
                .height(IntrinsicSize.Min) // measure children, then update height (required for correct accent bar height)
                .background(
                        if (isRecent) MaterialTheme.colorScheme.surfaceBright
                        else MaterialTheme.colorScheme.surfaceContainer)
                .padding(16.dp, 12.dp),
            verticalAlignment = Alignment.CenterVertically) {

            // accent bar
            Box(Modifier.width(6.dp).fillMaxHeight().background(accentColor))
            Spacer(Modifier.width(16.dp))

            // message, timestamp
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.Start) {
                Text(
                    text = text,
                    style = MaterialTheme.typography.titleMedium
                )
                Spacer(Modifier.height(4.dp))
                Text(
                    text = timestamp,
                    style = MaterialTheme.typography.bodyMedium
                )
            }

            // origin image
            Spacer(Modifier.width(edgePadding))
            OriginImage(origin, viewModel.isDarkMode)
        }
    }
}

@Composable
private fun OriginImage(
    origin: String,
    isDarkMode: Boolean) {

    val imageId = when (origin) {

        // streams
        nqStream -> R.drawable.google
        gcStream -> R.drawable.check_circle

        // user specific
        in tradingview -> {
            if (isDarkMode) R.drawable.tradingview_light
            else R.drawable.tradingview_dark
        }
        trendspider -> R.drawable.trendspider
        insomnia -> R.drawable.insomnia
        parsingErrorOrigin -> R.drawable.error
        else -> R.drawable.webhook
    }

    // origin
    Image(
        modifier = Modifier
            .size(settingsIconSize),
        painter = painterResource(imageId),
        contentDescription = null)
}

