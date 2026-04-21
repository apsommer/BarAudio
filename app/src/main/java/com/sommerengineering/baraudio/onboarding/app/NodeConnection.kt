package com.sommerengineering.baraudio.onboarding.app

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun NodeConnection() {

    Row(
        horizontalArrangement = Arrangement.spacedBy(24.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {

        repeat(3) { index ->

            Box(
                Modifier
                    .size(16.dp)
                    .background(
                        if (index == 1)
                            MaterialTheme.colorScheme.primary
                        else
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.5f),
                        CircleShape
                    )
            )

            if (index < 2) {
                Box(
                    Modifier
                        .width(24.dp)
                        .height(2.dp)
                        .background(MaterialTheme.colorScheme.primary.copy(alpha = 0.5f))
                )
            }
        }
    }
}