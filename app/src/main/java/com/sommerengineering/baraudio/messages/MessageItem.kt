package com.sommerengineering.baraudio.messages

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
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.sommerengineering.baraudio.MainViewModel
import com.sommerengineering.baraudio.R
import com.sommerengineering.baraudio.theme.AssetStyle
import com.sommerengineering.baraudio.theme.AssetStyles
import com.sommerengineering.baraudio.uitls.TimestampFormatter
import com.sommerengineering.baraudio.uitls.assetIconSize
import com.sommerengineering.baraudio.uitls.btcStream
import com.sommerengineering.baraudio.uitls.edgePadding
import com.sommerengineering.baraudio.uitls.esStream
import com.sommerengineering.baraudio.uitls.gcStream
import com.sommerengineering.baraudio.uitls.nqStream
import kotlinx.coroutines.delay

@Composable
fun MessageItem(
    viewModel: MainViewModel,
    isRecent: Boolean,
    modifier: Modifier,
    message: Message) {

    // extract attributes
    val timestamp = message.timestamp
    val text = message.message
    val origin = message.origin

    // style
    val style = resolveAssetStyle(origin, viewModel.isDarkMode)

    // update timestamp once per minute
    var beautifulTimestamp by remember { mutableStateOf("") }
    LaunchedEffect(Unit) {
        while (true) {
            beautifulTimestamp = TimestampFormatter.beautify(timestamp)
            val now = System.currentTimeMillis() // millis since epoch
            val delayMillis = 60_000L - (now % 60_000L) // millis remaining in current minute
            delay(delayMillis) // wait until next minute boundary
        }
    }

    Surface(modifier) {

        Row(
            modifier = Modifier
                .height(IntrinsicSize.Min) // measure children, then update height (required for correct accent bar height)
                .background(
                    if (isRecent) MaterialTheme.colorScheme.surfaceBright
                    else MaterialTheme.colorScheme.surfaceContainer
                )
                .padding(16.dp, 12.dp),
            verticalAlignment = Alignment.CenterVertically) {

            // accent bar
            Box(
                Modifier
                    .width(6.dp)
                    .fillMaxHeight()
                    .clip(RoundedCornerShape(3.dp))
                    .background(style.primary))
            Spacer(Modifier.width(16.dp))

            // message, timestamp
            Column(
                modifier = Modifier.weight(1f),
                horizontalAlignment = Alignment.Start) {
                Text(
                    text = text,
                    style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.height(4.dp))
                Text(
                    text = beautifulTimestamp,
                    style = MaterialTheme.typography.bodyMedium)
            }

            // origin image
            Spacer(Modifier.width(edgePadding))
            Icon(
                painter = painterResource(style.iconRes),
                contentDescription = null,
                tint = style.primary,
                modifier = Modifier.size(assetIconSize))
        }

        // divider between rows
        HorizontalDivider(
            thickness = 1.dp,
            color = MaterialTheme.colorScheme.outlineVariant,
            modifier = Modifier.padding(start = 22.dp))
    }
}

@Composable
fun resolveAssetStyle(
    origin: String,
    isDark: Boolean)= when (origin) {

        // streams
        nqStream -> AssetStyles.nq(isDark)
        esStream -> AssetStyles.es(isDark)
        btcStream -> AssetStyles.btc(isDark)
        gcStream -> AssetStyles.gc(isDark)

        // todo user specific
        else -> AssetStyle(
            primary = MaterialTheme.colorScheme.outline,
            accent  = MaterialTheme.colorScheme.outlineVariant,
            surface = MaterialTheme.colorScheme.surfaceContainer,
            text    = MaterialTheme.colorScheme.onSurface,
            iconRes = R.drawable.webhook)
//        in tradingview -> {
//            if (isDarkMode) R.drawable.tradingview_light
//            else R.drawable.tradingview_dark
//        }
//        trendspider -> R.drawable.trendspider
//        insomnia -> R.drawable.insomnia
//        parsingErrorOrigin -> R.drawable.error
//        else -> R.drawable.webhook
    }
