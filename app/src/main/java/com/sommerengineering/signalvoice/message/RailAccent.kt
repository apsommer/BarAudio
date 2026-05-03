package com.sommerengineering.signalvoice.message

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxHeight
import androidx.compose.foundation.layout.width
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.Color
import com.sommerengineering.signalvoice.uitls.rowAccentWidth
import com.sommerengineering.signalvoice.uitls.rowIconPadding

@Composable
fun RailAccent(color: Color) {

    Box(
        Modifier
            .width(rowAccentWidth)
            .fillMaxHeight()
    ) {

        Box(
            Modifier
                .align(Alignment.Center)
                .width(rowAccentWidth / 2)
                .fillMaxHeight()
                .background(color.copy(0.4f))
        )
    }

    Spacer(Modifier.width(rowIconPadding))
}