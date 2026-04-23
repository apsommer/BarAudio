package com.sommerengineering.baraudio.onboarding.app

import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.drawBehind
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.colorResource
import com.sommerengineering.baraudio.R

@Composable
fun AppOnboardingScreen(content: @Composable () -> Unit) {

    val glowColor = colorResource(R.color.app_blue)
    val alpha = 0.22f

    Box {

        // background
        Box(
            modifier = Modifier
                .fillMaxSize()
                .drawBehind {
                    val radius = size.minDimension * 0.6f

                    drawCircle(
                        brush = Brush.radialGradient(
                            colors = listOf(
                                glowColor.copy(alpha),
                                glowColor.copy(alpha * 0.35f),
                                Color.Transparent
                            ),
                            center = Offset(size.width / 2f, size.height * 0.55f),
                            radius = radius
                        ),
                        radius = radius,
                        center = Offset(size.width / 2f, size.height * 0.55f)
                    )
                }
        )

        Box(Modifier.align(Alignment.Center)) {
            content()
        }
    }
}