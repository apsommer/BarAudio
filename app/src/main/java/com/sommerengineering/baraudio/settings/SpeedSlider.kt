package com.sommerengineering.baraudio.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.width
import androidx.compose.material3.Slider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp

@Composable
fun SpeedSlider(
    onValueChanged: (Float) -> Unit
) {

    var position by remember { mutableFloatStateOf(0f) }

    Column(
        modifier = Modifier
            .padding(horizontal = 24.dp)
            .width(200.dp)
    ) {
        Slider(
            value = position,
            onValueChange = {
                position = it
                onValueChanged(it) }
        )
    }

}