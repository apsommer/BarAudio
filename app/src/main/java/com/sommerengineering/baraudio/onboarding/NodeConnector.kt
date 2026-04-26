package com.sommerengineering.baraudio.onboarding

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp

@Composable
fun NodeConnector(
    height: Dp,
    width: Dp,
) {

    val dotSize = 6.dp

    Box(
        modifier = Modifier
            .height(height)
            .width(dotSize),
        contentAlignment = Alignment.Center
    ) {

        // line
        Box(
            modifier = Modifier
                .align(Alignment.TopCenter)
                .height(height / 2 - dotSize / 2)
                .width(width)
                .background(MaterialTheme.colorScheme.outlineVariant)
        )

        // dot
        Box(
            modifier = Modifier
                .size(dotSize)
                .background(
                    MaterialTheme.colorScheme.primary,
                    shape = CircleShape
                )
        )

        // line
        Box(
            modifier = Modifier
                .align(Alignment.BottomCenter)
                .height(height / 2 - dotSize / 2)
                .width(width)
                .background(MaterialTheme.colorScheme.outlineVariant)
        )
    }
}