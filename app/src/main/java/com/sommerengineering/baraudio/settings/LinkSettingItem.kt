package com.sommerengineering.baraudio.settings

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
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
import androidx.compose.ui.draw.alpha
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.sommerengineering.baraudio.R
import com.sommerengineering.baraudio.theme.uiModeFadeTimeMillis
import kotlinx.coroutines.delay

const val howToUseFadeTimeMillis = 500

@Composable
fun LinkSettingItem(
    @DrawableRes icon: Int,
    @StringRes title: Int,
    onClick: () -> Unit) {

    // todo only fast "how to use" on first launch, then never again
    var isFade by remember { mutableStateOf(false) }

    val alpha by animateFloatAsState(
        targetValue = if (isFade) 0f else 1f,
        animationSpec = tween(
            durationMillis = howToUseFadeTimeMillis))

    if (title == R.string.how_to_use) {

        LaunchedEffect(false) {

            // fade out
            delay(howToUseFadeTimeMillis.toLong())
            isFade = true

            // fade in
            delay(howToUseFadeTimeMillis.toLong())
            isFade = false
        }
    }

    Surface {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .clickable { onClick() }
                .alpha(alpha), // animate
            verticalAlignment = Alignment.CenterVertically) {

            Box(
                modifier = Modifier.padding(24.dp)) {
                Icon(
                    modifier = Modifier
                        .size(24.dp),
                    painter = painterResource(icon),
                    contentDescription = null)
            }

            Column {
                Text(
                    text = stringResource(title),
                    style = MaterialTheme.typography.titleMedium)
            }
        }
    }
}