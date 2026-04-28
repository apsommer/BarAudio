package com.sommerengineering.signalvoice.settings

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Slider
import androidx.compose.runtime.Composable
import kotlin.math.roundToInt

@Composable
fun SliderImpl(
    initPosition: Float,
    onValueChanged: (Float) -> Unit,
    onValueChangeFinished: (Float) -> Unit
) {

    var position = initPosition

    Column {
        Slider(
            value = position,
            onValueChange = {
                position = (it * 10).roundToInt() / 10f // clean float artifacts
                onValueChanged(position)
            },
            onValueChangeFinished = {
                onValueChangeFinished(position)
            },
            valueRange = 0.5f..2f,
            steps = 14
        )
    }
}