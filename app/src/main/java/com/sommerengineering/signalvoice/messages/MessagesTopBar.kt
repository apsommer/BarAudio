package com.sommerengineering.signalvoice.messages

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.WindowInsets
import androidx.compose.foundation.layout.displayCutout
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalDensity
import androidx.compose.ui.platform.LocalLayoutDirection
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.sommerengineering.signalvoice.MainViewModel
import com.sommerengineering.signalvoice.R
import com.sommerengineering.signalvoice.uitls.appGreen
import com.sommerengineering.signalvoice.uitls.logoAlpha
import com.sommerengineering.signalvoice.uitls.rowHorizontalPadding

@Composable
fun MessagesTopBar(
    viewModel: MainViewModel,
    onSettingsClick: () -> Unit,
    onToggleFeedMode: () -> Unit,
    onToggleMute: () -> Unit
) {

    val isFullScreen = viewModel.isFullScreen
    val feedMode = viewModel.feedMode
    val isMute by viewModel.isMute.collectAsState()

    // full screen toggles logo size to accommodate notches/cutouts
    val basePadding = rowHorizontalPadding / 2
    val (cutoutStart, cutoutEnd) = getCutoutPadding(isFullScreen)
    val logoIcon = if (isFullScreen) R.drawable.appbar_compact else R.drawable.appbar

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(
                start = basePadding + cutoutStart,
                end = basePadding + cutoutEnd,
                top = basePadding,
                bottom = basePadding
            ),
        verticalAlignment = Alignment.CenterVertically
    ) {

        // settings drawer
        AppBarIcon(
            iconRes = R.drawable.drawer,
            onClick = onSettingsClick,
            modifier = Modifier
                .padding(end = 2.dp)
                .rotate(180f)
        )

        // logo, with scrim overlay
        Box(Modifier.weight(1f)) {
            Image(
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .height(22.dp),
                painter = painterResource(logoIcon),
                contentDescription = null
            )
            Box(
                Modifier
                    .matchParentSize()
                    .background(MaterialTheme.colorScheme.background.copy(logoAlpha))
            )
        }

        // feed mode
        AppBarIcon(
            iconRes =
                if (feedMode == FeedMode.Linear) R.drawable.group
                else R.drawable.ungroup,
            iconTint = MaterialTheme.colorScheme.onSurface,
            backgroundColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.03f),
            borderColor = MaterialTheme.colorScheme.onSurface.copy(alpha = 0.08f),
            onClick = onToggleFeedMode
        )

        Spacer(Modifier.width(6.dp))

        // listening
        AppBarIcon(
            iconRes =
                if (isMute) R.drawable.listening_off
                else R.drawable.listening_on,
            iconTint =
                if (!isMute) appGreen()
                else MaterialTheme.colorScheme.onSurface.copy(0.6f),
            backgroundColor =
                if (!isMute) appGreen().copy(alpha = 0.15f)
                else appGreen().copy(alpha = 0.04f),
            borderColor =
                if (!isMute) appGreen().copy(alpha = 0.4f)
                else MaterialTheme.colorScheme.onSurface.copy(alpha = 0.10f),
            onClick = onToggleMute
        )
    }
}

@Composable
fun AppBarIcon(
    iconRes: Int,
    iconTint: Color = Color.Unspecified,
    backgroundColor: Color = Color.Transparent,
    borderColor: Color = Color.Transparent,
    onClick: () -> Unit,
    modifier: Modifier = Modifier
) {

    val iconSize = 26.dp
    val rippleSize = 44.dp
    val shape = RoundedCornerShape(12.dp)

    Box(
        modifier = modifier
            .size(rippleSize)
            .clip(shape)
            .background(backgroundColor)
            .border(
                width = 1.dp,
                color = borderColor,
                shape = shape
            )
            .clickable(interactionSource = remember { MutableInteractionSource() }) { onClick() },
        contentAlignment = Alignment.Center
    ) {
        Icon(
            modifier = Modifier
                .size(iconSize)
                .padding(horizontal = 2.dp),
            painter = painterResource(iconRes),
            tint = iconTint,
            contentDescription = null
        )
    }
}

@Composable
fun getCutoutPadding(isFullScreen: Boolean): Pair<Dp, Dp> {

    // fullscreen
    if (isFullScreen) return 0.dp to 0.dp

    val density = LocalDensity.current
    val layoutDirection = LocalLayoutDirection.current
    val cutout = WindowInsets.displayCutout

    val leftPx = cutout.getLeft(density, layoutDirection)
    val rightPx = cutout.getRight(density, layoutDirection)

    val left = (leftPx / density.density).dp
    val right = (rightPx / density.density).dp

    return left to right
}