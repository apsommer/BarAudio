package com.sommerengineering.baraudio.message

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.sommerengineering.baraudio.uitls.rowAccentWidth

@Composable
fun Rail(color: Color) {
    Box(Modifier
        .width(rowAccentWidth)
        .fillMaxHeight()) {
        Box(Modifier
            .align(Alignment.Center)
            .width(rowAccentWidth / 2)
            .fillMaxHeight()
            .background(color.copy(0.6f))) }
}