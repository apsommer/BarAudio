package com.sommerengineering.baraudio.message

import androidx.compose.ui.graphics.Color
import com.sommerengineering.baraudio.source.MessageOrigin

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