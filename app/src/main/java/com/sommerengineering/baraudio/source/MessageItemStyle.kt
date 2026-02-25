package com.sommerengineering.baraudio.source

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

data class MessageItemStyle(
    val primary: Color,
    val accent: Color,
    val surface: Color,
    val text: Color,
    val iconRes: Int
)

@Composable
fun resolveMessageStyle(
    origin: MessageOrigin,
    isDarkMode: Boolean) = when (origin) {

    is MessageOrigin.Stream -> origin.asset.style(isDarkMode)
    is MessageOrigin.UserSignal -> origin.source.style(isDarkMode)
}
