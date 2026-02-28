package com.sommerengineering.baraudio.source

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color

data class ItemStyle(
    val primary: Color,
    val accent: Color,
    val surface: Color,
    val text: Color,
    val iconRes: Int,
    val isIconTinted: Boolean = true
)

@Composable
fun resolveMessageStyle(
    origin: MessageOrigin,
    isDarkMode: Boolean) = when (origin) {

    is MessageOrigin.BroadcastStream -> origin.asset.style(isDarkMode)
    is MessageOrigin.UserSignal -> origin.source.style(isDarkMode)
}
