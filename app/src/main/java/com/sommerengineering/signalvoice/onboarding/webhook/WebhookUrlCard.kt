package com.sommerengineering.signalvoice.onboarding.webhook

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.sommerengineering.signalvoice.R
import com.sommerengineering.signalvoice.theme.monospacedFontFamily
import com.sommerengineering.signalvoice.uitls.appBlue
import com.sommerengineering.signalvoice.uitls.edgePadding

@Composable
fun WebhookUrlCard(
    webhookUrl: String,
    onCopyClick: () -> Unit
) {

    // clarify spacing of long url
    val formattedUrl = webhookUrl
        .replace("com-", "com\n-")
        .replace("signalvoice.", "signalvoice\n.")
        .replace("/signal?", "/signal?\n")

    Column(
        modifier = Modifier
            .fillMaxWidth()
            .clip(RoundedCornerShape(16.dp))
            .background(MaterialTheme.colorScheme.surfaceContainer)
            .border(
                width = 1.dp,
                color = appBlue().copy(alpha = 0.08f),
                shape = RoundedCornerShape(16.dp)
            )
            .padding(edgePadding / 2)
    ) {

        // container for webhook url
        Box(
            modifier = Modifier
                .fillMaxWidth()
                .clip(RoundedCornerShape(12.dp))
                .border(
                    width = 1.dp,
                    color = MaterialTheme.colorScheme.outline.copy(alpha = 0.2f),
                    shape = RoundedCornerShape(12.dp)
                )
                .background(MaterialTheme.colorScheme.surface.copy(alpha = 0.6f))
                .clickable(onClick = onCopyClick)
                .padding(edgePadding / 2)
        ) {

            Text(
                text = formattedUrl,
                fontFamily = monospacedFontFamily,
                style = MaterialTheme.typography.bodyMedium,
                color = MaterialTheme.colorScheme.onSurface,
                modifier = Modifier.padding(edgePadding / 2)
            )
        }

        // footer, with icon
        Row(
            verticalAlignment = Alignment.CenterVertically,
            modifier = Modifier
                .padding(top = edgePadding / 2)
        ) {

            Icon(
                painter = painterResource(R.drawable.shield),
                contentDescription = null,
                tint = MaterialTheme.colorScheme.onSurfaceVariant,
                modifier = Modifier.size(16.dp)
            )

            Spacer(Modifier.width(6.dp))

            Text(
                text = "Keep this URL private and secure.",
                style = MaterialTheme.typography.bodySmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant
            )
        }
    }
}