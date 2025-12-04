package com.sommerengineering.baraudio.messages

import android.content.Context
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
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.FloatingActionButton
import androidx.compose.material3.Icon
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.sommerengineering.baraudio.MainViewModel
import com.sommerengineering.baraudio.R
import com.sommerengineering.baraudio.fabButtonSize
import com.sommerengineering.baraudio.loginButtonSize

@Composable
fun MessagesFloatingActionButton(
    context: Context,
    viewModel: MainViewModel) {

    FloatingActionButton (
        modifier = Modifier
            .size(fabButtonSize)
            .border(
                border = BorderStroke(
                    width = 1.dp,
                    color = viewModel.getFabIconColor()),
                shape = CircleShape),
        containerColor = viewModel.getFabBackgroundColor(),
        shape = CircleShape,
        onClick = { viewModel.toggleMute(context) }) {

        // progress wheel
        if (viewModel.shouldShowSpinner) {
            CircularProgressIndicator()
            return@FloatingActionButton
        }

        // animate toggle icon
        AnimatedContent (
            targetState = viewModel.isMute,
            transitionSpec = {
                fadeIn(spring(stiffness = Spring.StiffnessVeryLow))
                    .togetherWith(
                        fadeOut(spring(stiffness = Spring.StiffnessVeryLow)))
            },
            label = ""

        ) { targetState ->

            // mute
            if (targetState) {
                Icon(
                    modifier = Modifier
                        .size(fabButtonSize * 0.4f),
                    painter = painterResource(R.drawable.volume_off),
                    tint = viewModel.getFabIconColor(),
                    contentDescription = null)

                return@AnimatedContent
            }

            // unmute
            Icon(
                modifier = Modifier
                    .size(fabButtonSize * 0.4f),
                painter = painterResource(R.drawable.volume_on),
                tint = viewModel.getFabIconColor(),
                contentDescription = null)
        }
    }
}