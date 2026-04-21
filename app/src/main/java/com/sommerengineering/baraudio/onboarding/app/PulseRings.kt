package com.sommerengineering.baraudio.onboarding.app

import androidx.compose.foundation.background
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun PulseRings() {

    Box(contentAlignment = Alignment.Center) {

        repeat(3) { i ->
            Box(
                Modifier
                    .size((80 + i * 40).dp)
                    .border(
                        width = 2.dp,
                        color = MaterialTheme.colorScheme.primary.copy(alpha = 0.4f - i * 0.1f),
                        shape = CircleShape
                    )
            )
        }

        Box(
            Modifier
                .size(12.dp)
                .background(MaterialTheme.colorScheme.primary, CircleShape)
        )
    }
}