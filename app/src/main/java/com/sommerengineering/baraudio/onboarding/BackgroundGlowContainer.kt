package com.sommerengineering.baraudio.onboarding

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import com.sommerengineering.baraudio.uitls.appBlue

@Composable
fun BackgroundGlowContainer(content: @Composable () -> Unit) {

    val glowColor = appBlue()

    Box {

        // background glow
        Box(
            modifier = Modifier
                .fillMaxSize()
                .drawBehind {

                    val center = Offset(size.width / 2f, size.height / 2f)
                    val radius = size.minDimension * 0.55f

                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                glowColor.copy(alpha = 0.18f),
                                glowColor.copy(alpha = 0.10f),
                                glowColor.copy(alpha = 0.04f),
                                Color.Transparent
                            ),
                            center = center,
                            radius = radius
                        ),
                        radius = radius,
                        center = center
                    )
                }
        )

        // content
        Box(
            modifier = Modifier.align(Alignment.Center)
        ) {
            content()
        }
    }
}