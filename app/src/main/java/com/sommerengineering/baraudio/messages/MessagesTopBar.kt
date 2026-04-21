package com.sommerengineering.baraudio.messages

import androidx.compose.foundation.Image
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.interaction.MutableInteractionSource
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.offset
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.draw.rotate
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.sommerengineering.baraudio.MainViewModel
import com.sommerengineering.baraudio.R
import com.sommerengineering.baraudio.uitls.logoAlpha
import com.sommerengineering.baraudio.uitls.rowHorizontalPadding

@Composable
fun MessagesTopBar(
    viewModel: MainViewModel,
    onSettingsClick: () -> Unit,
    onToggleFeedMode: () -> Unit,
    onToggleMute: () -> Unit) {

    // feed mode
    val feedMode = viewModel.feedMode
    val feedModeIcon = if (feedMode == FeedMode.Linear) R.drawable.group  else R.drawable.ungroup

    // mute
    val isMute = viewModel.isMute
    val muteIcon = if (isMute) R.drawable.mute else R.drawable.unmute
    val muteAlpa = if (isMute) 0.5f else 0.7f

    // icon size
    val iconSize = 28.dp
    val muteIconSize = 24.dp

    Row(
        modifier = Modifier
            .fillMaxWidth()
            .padding(rowHorizontalPadding / 2),
        verticalAlignment = Alignment.CenterVertically) {

        // settings drawer
        AppBarIcon(
            iconRes = R.drawable.drawer,
            iconSize = iconSize,
            onClick = onSettingsClick,
            modifier = Modifier
                .padding(end = 2.dp)
                .rotate(180f))

        // logo, with scrim overlay
        Box(Modifier.weight(1f)) {
            Image(
                modifier = Modifier
                    .align(Alignment.CenterStart)
                    .height(22.dp),
                painter = painterResource(R.drawable.appbar),
                contentDescription = null)
            Box(Modifier
                .matchParentSize()
                .background(MaterialTheme.colorScheme.background.copy(logoAlpha)))
        }

        // feed mode
        AppBarIcon(
            iconRes = feedModeIcon,
            iconSize = iconSize,
            iconAlpha = 0.7f,
            onClick = onToggleFeedMode,
            modifier = Modifier.offset(x = iconSize / 2))

        // mute
        AppBarIcon(
            iconRes = muteIcon,
            iconSize = muteIconSize,
            iconAlpha = muteAlpa,
            onClick = onToggleMute)
    }
}

@Composable
fun AppBarIcon(
    iconRes: Int,
    iconSize: Dp,
    iconAlpha: Float = 1f,
    onClick: () -> Unit,
    modifier: Modifier = Modifier) {

    val rippleSize = 48.dp

    Box(
        modifier = modifier
            .size(rippleSize)
            .clip(CircleShape)
            .clickable(interactionSource = remember { MutableInteractionSource() }) { onClick() },
        contentAlignment = Alignment.Center) {
        Icon(
            modifier = Modifier.size(iconSize),
            painter = painterResource(iconRes),
            tint = MaterialTheme.colorScheme.onSurface.copy(iconAlpha),
            contentDescription = null)
    }
}