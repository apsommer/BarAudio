package com.sommerengineering.baraudio.onboarding.app

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.unit.dp

@Composable
fun VoicePulseGraphic() {

    Row(
        horizontalArrangement = Arrangement.spacedBy(6.dp),
        verticalAlignment = Alignment.CenterVertically
    ) {
        repeat(9) { index ->

            val height = when (index) {
                4 -> 64.dp
                3,5 -> 40.dp
                2,6 -> 28.dp
                else -> 16.dp
            }

            Box(
                Modifier
                    .width(4.dp)
                    .height(height)
                    .clip(RoundedCornerShape(2.dp))
                    .background(
                        if (index == 4)
                            MaterialTheme.colorScheme.primary
                        else
                            MaterialTheme.colorScheme.primary.copy(alpha = 0.5f)
                    )
            )
        }
    }
}