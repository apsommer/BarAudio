package com.sommerengineering.baraudio.messages

import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.IconButtonDefaults
import androidx.compose.material3.LocalContentColor
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.sommerengineering.baraudio.R
import com.sommerengineering.baraudio.uitls.appBlue

@Composable
fun EmptyStateCard(
    onLaunchWebhookOnboarding: () -> Unit,
    onDismiss: () -> Unit
) {

    InlineActionCard(
        title = "Custom signal",
        subtitle = "Set up your webhook to receive alerts →",
        onClick = onLaunchWebhookOnboarding,
        showClose = true,
        onDismiss = onDismiss,
        iconRes = R.drawable.webhook
    )
}

@Composable
fun NotificationsDisabledCard(
    onOpenSettings: () -> Unit
) {

    InlineActionCard(
        title = "Notifications are off",
        subtitle = "Enable to receive real-time alerts",
        onClick = onOpenSettings,
        iconRes = R.drawable.webhook // or bell
    )
}

@Composable
fun InlineActionCard(
    title: String,
    subtitle: String,
    onClick: () -> Unit,
    modifier: Modifier = Modifier,
    showClose: Boolean = false,
    onDismiss: (() -> Unit)? = null,
    iconRes: Int? = null
) {

    val shape = RoundedCornerShape(16.dp)

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 10.dp)
            .clip(shape)
            .clickable { onClick() }
            .border(
                width = 1.dp,
                color = appBlue().copy(alpha = 0.14f),
                shape = shape
            ),
        shape = shape,
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.surfaceContainerHigh
        )
    ) {

        Box {

            Row(
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(20.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {

                // ICON (now actually used)
                if (iconRes != null) {
                    Icon(
                        painter = painterResource(iconRes),
                        contentDescription = null,
                        tint = appBlue(),
                        modifier = Modifier.size(20.dp)
                    )

                    Spacer(Modifier.width(12.dp))
                }

                Column(
                    modifier = Modifier.weight(1f)
                ) {

                    Text(
                        text = title,
                        style = MaterialTheme.typography.titleMedium,
                        color = MaterialTheme.colorScheme.onSurface
                    )

                    Spacer(Modifier.height(6.dp))

                    Text(
                        text = subtitle,
                        style = MaterialTheme.typography.bodyMedium,
                        color = MaterialTheme.colorScheme.onSurfaceVariant.copy(alpha = 0.85f)
                    )
                }
            }

            if (showClose && onDismiss != null) {
                IconButton(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(top = 6.dp, end = 6.dp)
                        .size(36.dp),
                    onClick = onDismiss,
                    colors = IconButtonDefaults.iconButtonColors(
                        contentColor = LocalContentColor.current.copy(alpha = 0.6f)
                    )
                ) {
                    Icon(
                        painter = painterResource(R.drawable.close),
                        contentDescription = null
                    )
                }
            }
        }
    }
}