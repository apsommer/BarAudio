package com.sommerengineering.baraudio.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Slider
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.setValue

@Composable
fun SliderImpl(
    initPosition: Float,
    onValueChanged: (Float) -> Unit,
    onValueChangeFinished: () -> Unit
) {

    var position by remember { mutableFloatStateOf(initPosition) }

    Column{
        Slider(
            value = position,
            onValueChange = {
                position = it
                onValueChanged(it)
            },
            onValueChangeFinished = {
                onValueChangeFinished()
            },
            valueRange = 0.5f..2f,
            steps = 14)
    }
}