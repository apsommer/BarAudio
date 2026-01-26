package com.sommerengineering.baraudio.messages

import android.content.Context
import android.content.Intent
import android.provider.Settings
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import com.sommerengineering.baraudio.BuildConfig
import com.sommerengineering.baraudio.allowNotificationsTitle
import com.sommerengineering.baraudio.channelId
import com.sommerengineering.baraudio.colorTransitionTimeMillis
import com.sommerengineering.baraudio.edgePadding

@Composable
fun AllowNotificationsBottomBar(
    areNotificationsAllowed: Boolean
) {

    val context = LocalContext.current

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
                    onClick = { launchSystemNotificationSettings(context) })) {

            Text(
                modifier = Modifier
                    .fillMaxWidth()
                    .background(Color.Red)
                    .padding(horizontal = edgePadding, vertical = 12.dp),
                text = allowNotificationsTitle,
                textAlign = TextAlign.Center,
                style = MaterialTheme.typography.titleMedium,
                color = Color.White)
        }
    }
}

fun launchSystemNotificationSettings(context: Context) =

    context.startActivity(
        Intent(Settings.ACTION_CHANNEL_NOTIFICATION_SETTINGS)
            .putExtra(Settings.EXTRA_APP_PACKAGE, BuildConfig.APPLICATION_ID)
            .putExtra(Settings.EXTRA_CHANNEL_ID, channelId))
