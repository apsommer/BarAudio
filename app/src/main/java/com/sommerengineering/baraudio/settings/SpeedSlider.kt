package com.sommerengineering.baraudio.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Slider
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun SpeedSlider() {

    var position by remember { mutableFloatStateOf(0f) }

    Column(
        modifier = Modifier.width(300.dp)
    ) {
        Slider(
            value = position,
            onValueChange = { position = it }
        )
        Text(text = position.toString())
    }
}