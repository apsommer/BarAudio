package com.sommerengineering.signalvoice.source

import com.sommerengineering.signalvoice.message.MessageItemStyle
import com.sommerengineering.signalvoice.uitls.userSignalDescription

data class Source(
    val key: String,
    val displayName: String,
    val order: Int,
    val style: MessageItemStyle,
    val description: String = userSignalDescription,
    val signalDescription: String = userSignalDescription
)