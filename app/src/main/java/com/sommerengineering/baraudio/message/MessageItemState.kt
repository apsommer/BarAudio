package com.sommerengineering.baraudio.message

import androidx.compose.ui.graphics.Color
import com.sommerengineering.baraudio.source.MessageOrigin

data class MessageItemState(
    val text: String, // todo collapse to just message
    val timestamp: String, // todo collapse to just message
    val beautifulTimestamp: String, // todo collapse to just message
    val origin: MessageOrigin, // todo collapse to just message
    val style: MessageItemStyle, // todo collapse to just message
    val isExpanded: Boolean,
    val backgroundColor: Color,
    val onClick: () -> Unit,
    val onLongClick: () -> Unit
)