package com.sommerengineering.signalvoice.messages

import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.unit.dp
import com.sommerengineering.signalvoice.uitls.TimestampFormatter
import com.sommerengineering.signalvoice.uitls.appGreen
import kotlinx.coroutines.delay

@Composable
fun LastSignalPulse(
    modifier: Modifier = Modifier,
    timestamp: String
) {

    // update timestamp once per minute
    var beautifulTimestamp by remember { mutableStateOf("") }
    LaunchedEffect(timestamp) {
        while (true) {
            beautifulTimestamp = TimestampFormatter.beautifyCompact(timestamp)
            val now = System.currentTimeMillis() // millis since epoch
            val delayMillis = 60_000L - (now % 60_000L) // millis remaining in current minute
            delay(delayMillis) // wait until next minute boundary
        }
    }

    val displayText = "last signal · $beautifulTimestamp"

    Box(
        modifier = modifier,
        contentAlignment = Alignment.Center
    ) {
        Row(
            modifier = Modifier
                .align(Alignment.Center)
                .fillMaxWidth(),
            horizontalArrangement = Arrangement.Center,
            verticalAlignment = Alignment.CenterVertically
        ) {

            ListeningDot()
            Spacer(Modifier.width(6.dp))
            Text(
                text = displayText,
                color = MaterialTheme.colorScheme.onBackground.copy(0.5f),
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Composable
fun ListeningDot() {

    val pulseDurationMillis = 1000
    val infiniteTransition = rememberInfiniteTransition()

    val alpha by infiniteTransition.animateFloat(
        initialValue = 0.35f,
        targetValue = 0.75f,
        animationSpec = infiniteRepeatable(
            animation = tween(pulseDurationMillis),
            repeatMode = RepeatMode.Reverse
        )
    )

    Box(
        Modifier
            .size(4.dp)
            .alpha(alpha)
            .background(
                color = appGreen().copy(0.7f),
                shape = CircleShape
            )
    )
}