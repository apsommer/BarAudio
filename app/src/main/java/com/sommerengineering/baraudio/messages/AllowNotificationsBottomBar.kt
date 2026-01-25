package com.sommerengineering.baraudio.messages

import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.core.tween
import androidx.compose.animation.slideInVertically
import androidx.compose.animation.slideOutVertically
import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.sommerengineering.baraudio.allowNotificationsTitle
import com.sommerengineering.baraudio.colorTransitionTimeMillis
import com.sommerengineering.baraudio.utils.initRetrofit
import com.sommerengineering.baraudio.utils.logMessage

@Composable
fun AllowNotificationsBottomBar(
    areNotificationsAllowed: Boolean
) {

    AnimatedVisibility(
        visible = !areNotificationsAllowed,
        enter = slideInVertically(
            initialOffsetY = { it },
            animationSpec = tween(colorTransitionTimeMillis)),
        exit = slideOutVertically(
            targetOffsetY = { it },
            animationSpec = tween(colorTransitionTimeMillis))) {

        Row(
            modifier = Modifier
                .fillMaxWidth()
                .clickable(
                    onClick = {
                        logMessage("Clicked banner ...")
                    })) {

            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.Red)
                    .padding(horizontal = 0.dp, vertical = 8.dp)
                    .clickable(
                        onClick = {
                            logMessage("Clicked banner ...")

                        }
                    ),
                text = allowNotificationsTitle,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleMedium,
                color = Color.White)
        }
    }
}