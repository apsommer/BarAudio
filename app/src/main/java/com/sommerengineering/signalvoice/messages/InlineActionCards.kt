package com.sommerengineering.signalvoice.messages

import android.content.Intent
import android.provider.Settings
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
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sommerengineering.signalvoice.MainViewModel
import com.sommerengineering.signalvoice.R
import com.sommerengineering.signalvoice.Session.Authenticated
import com.sommerengineering.signalvoice.uitls.appBlue
import com.sommerengineering.signalvoice.uitls.emptyStateSubtitle
import com.sommerengineering.signalvoice.uitls.emptyStateTitle
import com.sommerengineering.signalvoice.uitls.guestEmptyStateSubtitle
import com.sommerengineering.signalvoice.uitls.notificationsDisabledSubtitle
import com.sommerengineering.signalvoice.uitls.notificationsDisabledTitle
import com.sommerengineering.signalvoice.uitls.settingsIconSize

@Composable
fun EmptyStateCard(
    viewModel: MainViewModel,
    onLaunchWebhookOnboarding: () -> Unit,
    onNavigateToLogin: () -> Unit
) {

    val session = viewModel.session
    val onDismiss = { viewModel.updateEmptyState(false) }

    InlineActionCard(
        title = emptyStateTitle,
        subtitle =
            if (session is Authenticated) emptyStateSubtitle
            else guestEmptyStateSubtitle,
        onClick =
            if (session is Authenticated) onLaunchWebhookOnboarding
            else onNavigateToLogin,
        showClose = true,
        onDismiss = onDismiss,
        iconRes = R.drawable.webhook
    )
}

@Composable
fun NotificationsDisabledCard() {

    val context = LocalContext.current
    val onOpenSettings = {
        context.startActivity(
            Intent(Settings.ACTION_APP_NOTIFICATION_SETTINGS).apply {
                putExtra(
                    Settings.EXTRA_APP_PACKAGE,
                    context.packageName
                )
            }
        )
    }

    InlineActionCard(
        title = notificationsDisabledTitle,
        subtitle = notificationsDisabledSubtitle,
        onClick = onOpenSettings,
        iconRes = R.drawable.notifications,
        textStyle = MaterialTheme.typography.titleMedium.copy(fontWeight = FontWeight.Bold)
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
    iconRes: Int? = null,
    textStyle: TextStyle = MaterialTheme.typography.titleMedium
) {

    val shape = RoundedCornerShape(16.dp)

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 8.dp)
            .clip(shape)
            .clickable { onClick() }
            .border(
                width = 1.dp,
                color = appBlue().copy(alpha = 0.18f),
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
                    .padding(18.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {

                // icon
                if (iconRes != null) {
                    Icon(
                        painter = painterResource(iconRes),
                        contentDescription = null,
                        tint = appBlue().copy(alpha = 0.9f),
                        modifier = Modifier.size(settingsIconSize)
                    )

                    Spacer(Modifier.width(12.dp))
                }

                // title, subtitle
                Column(
                    modifier = Modifier.weight(1f)
                ) {
                    Text(
                        text = title,
                        style = textStyle,
                        fontSize = 17.sp,
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

            // close
            if (showClose && onDismiss != null) {
                IconButton(
                    modifier = Modifier
                        .align(Alignment.TopEnd)
                        .padding(top = 6.dp, end = 6.dp)
                        .size(32.dp),
                    onClick = onDismiss,
                    colors = IconButtonDefaults.iconButtonColors(
                        contentColor = LocalContentColor.current.copy(alpha = 0.5f)
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