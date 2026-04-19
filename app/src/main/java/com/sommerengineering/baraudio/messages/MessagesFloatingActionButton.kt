package com.sommerengineering.baraudio.messages

import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.FloatingActionButtonDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.sommerengineering.baraudio.MainViewModel
import com.sommerengineering.baraudio.R
import com.sommerengineering.baraudio.uitls.fabSize

@Composable
fun MessagesFloatingActionButton(
    viewModel: MainViewModel) {

    val isMute = viewModel.isMute

    val containerColor = if (isMute) MaterialTheme.colorScheme.surface else MaterialTheme.colorScheme.primaryContainer
    val iconRes = if (isMute) R.drawable.mute else R.drawable.unmute
    val iconSize = if (isMute) fabSize * 0.45f else fabSize * 0.5f
    val iconColor = if (isMute) MaterialTheme.colorScheme.onSurfaceVariant else MaterialTheme.colorScheme.onPrimaryContainer

    FloatingActionButton (
        modifier = Modifier
            .size(fabSize)
            .border(BorderStroke(1.dp, iconColor), CircleShape),
        containerColor = containerColor,
        shape = CircleShape,
        elevation = FloatingActionButtonDefaults.elevation(
            defaultElevation = 2.dp,
            pressedElevation = 4.dp),
        onClick = { viewModel.toggleMute() }) {

        Icon(
            modifier = Modifier.size(iconSize),
            painter = painterResource(iconRes),
            tint = iconColor,
            contentDescription = null)
    }
}
