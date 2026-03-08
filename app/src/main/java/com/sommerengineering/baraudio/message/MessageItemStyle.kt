package com.sommerengineering.baraudio.message

import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.sommerengineering.baraudio.source.MessageOrigin

data class MessageItemStyle(
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

data class MessageItemState(
    val text: String,
    val timestamp: String,
    val beautifulTimestamp: String,
    val origin: MessageOrigin,
    val style: MessageItemStyle,
    val isExpanded: Boolean,
    val backgroundColor: Color,
    val onClick: () -> Unit,
    val onLongClick: () -> Unit
)