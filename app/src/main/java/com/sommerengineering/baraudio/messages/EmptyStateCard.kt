package com.sommerengineering.baraudio.messages

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.sommerengineering.baraudio.R

@Composable
fun EmptyStateCard(
    onClick: () -> Unit,
    onDismiss: () -> Unit,
    modifier: Modifier = Modifier) {

    Card(
        modifier = modifier
            .fillMaxWidth()
            .padding(horizontal = 16.dp, vertical = 12.dp)
            .clickable { onClick() },
        shape = RoundedCornerShape(16.dp),
        elevation = CardDefaults.cardElevation(defaultElevation = 2.dp)) {

        Box {

            // title, subtitle
            Column(Modifier
                .padding(20.dp)
                .fillMaxWidth()) {
                Text(
                    text = "Custom signal",
                    style = MaterialTheme.typography.titleMedium)
                Spacer(Modifier.size(6.dp))
                Text(
                    text = "Set up your webhook to receive alerts",
                    style = MaterialTheme.typography.bodyMedium,
                    color = MaterialTheme.colorScheme.onSurfaceVariant)
            }

            // close button
            IconButton(
                onClick = onDismiss,
                modifier = Modifier.align(Alignment.TopEnd)) {
                Icon(
                    painter = painterResource(R.drawable.close),
                    contentDescription = null)
            }
        }
    }
}