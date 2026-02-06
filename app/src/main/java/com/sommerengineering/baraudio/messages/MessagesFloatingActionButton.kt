package com.sommerengineering.baraudio.messages

import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.core.Spring
import androidx.compose.animation.core.spring
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.sommerengineering.baraudio.MainViewModel
import com.sommerengineering.baraudio.R
import com.sommerengineering.baraudio.fabButtonSize

@Composable
fun MessagesFloatingActionButton(
    viewModel: MainViewModel) {

    val isMute by viewModel.isMute.collectAsState()

    val iconColor =
        if (isMute) MaterialTheme.colorScheme.outline
        else MaterialTheme.colorScheme.onPrimaryContainer

    val backgroundColor =
        if (isMute) MaterialTheme.colorScheme.surfaceVariant
        else MaterialTheme.colorScheme.primaryContainer

    FloatingActionButton (
        modifier = Modifier
            .size(fabButtonSize)
            .border(
                border = BorderStroke(
                    width = 1.dp,
                    color = iconColor),
                shape = CircleShape),
        containerColor = backgroundColor,
        shape = CircleShape,
        onClick = { viewModel.toggleMute() }) {

        // animate toggle icon
        AnimatedContent (
            targetState = isMute,
            transitionSpec = {
                fadeIn(spring(stiffness = Spring.StiffnessVeryLow))
                    .togetherWith(
                        fadeOut(spring(stiffness = Spring.StiffnessVeryLow)))
            },
            label = "") { targetState ->

            // mute
            if (targetState) {
                Icon(
                    modifier = Modifier
                        .size(fabButtonSize * 0.5f),
                    painter = painterResource(R.drawable.volume_off),
                    tint = iconColor,
                    contentDescription = null)

                return@AnimatedContent
            }

            // unmute
            Icon(
                modifier = Modifier
                    .size(fabButtonSize * 0.5f),
                painter = painterResource(R.drawable.volume_on),
                tint = iconColor,
                contentDescription = null)
        }
    }
}
