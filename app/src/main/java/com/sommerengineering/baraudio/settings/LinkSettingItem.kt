package com.sommerengineering.baraudio.settings

import androidx.annotation.DrawableRes
import androidx.annotation.StringRes
import androidx.compose.animation.Animatable
import androidx.compose.animation.core.tween
import androidx.compose.foundation.Image
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.unit.dp
import com.sommerengineering.baraudio.theme.darkScheme
import com.sommerengineering.baraudio.theme.fadeTimeMillis
import com.sommerengineering.baraudio.theme.lightScheme


@Composable
fun LinkSettingItem(
    @DrawableRes icon: Int,
    @StringRes title: Int,
    onClick: () -> Unit) {

    val currentColor = MaterialTheme.colorScheme.onSurface
    val targetColor =
        if (currentColor == lightScheme.onSurface) darkScheme.onSurface
        else lightScheme.onSurface

    val color = remember { Animatable(currentColor) }

    LaunchedEffect(false) {
        color.animateTo(
            targetValue = targetColor,
            animationSpec = tween(
                durationMillis = 1000))
        color.animateTo(
            targetValue = currentColor,
            animationSpec = tween(
                durationMillis = 1000))
    }

    Surface {
        Row(
            modifier = Modifier
                .fillMaxSize()
                .clickable { onClick() },
            verticalAlignment = Alignment.CenterVertically) {
            Box(
                modifier = Modifier
                    .padding(24.dp)) {
                Image(
                    modifier = Modifier
                        .size(24.dp),
                    painter = painterResource(icon),
                    contentDescription = null)
            }
            Column {
                Text(
                    text = stringResource(title),
                    style = MaterialTheme.typography.titleMedium,
                    color = color.value)
            }
        }
    }
}