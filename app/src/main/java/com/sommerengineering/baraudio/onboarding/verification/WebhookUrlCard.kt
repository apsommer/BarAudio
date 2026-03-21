package com.sommerengineering.baraudio.onboarding.verification

import androidx.compose.foundation.background
import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import com.sommerengineering.baraudio.MainViewModel
import com.sommerengineering.baraudio.theme.monospacedFontFamily

@Composable
fun WebhookUrlCard(
    viewModel: MainViewModel,
    onClick: () -> Unit) {

    // clarify spacing of long url
    val formattedUrl = viewModel.webhookUrl
        .replace("com-", "com\n-")
        .replace("baraudio.", "baraudio\n.")
        .replace("/baraudio?", "/baraudio?\n")

    // simple card background
    Box(Modifier
        .fillMaxWidth()
        .padding(horizontal = 24.dp)
        .clip(RoundedCornerShape(12.dp))
        .background(MaterialTheme.colorScheme.surfaceVariant)
        .clickable(onClick = onClick)
        .padding(16.dp)) {

        // formatted webhook url
        Text(
            text = formattedUrl,
            fontFamily = monospacedFontFamily,
            style = MaterialTheme.typography.bodyMedium,
            color = MaterialTheme.colorScheme.onSurfaceVariant,
            lineHeight = 24.sp)
    }
}