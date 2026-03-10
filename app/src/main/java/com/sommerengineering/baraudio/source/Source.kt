package com.sommerengineering.baraudio.source

import com.sommerengineering.baraudio.message.MessageItemStyle
import com.sommerengineering.baraudio.uitls.userSignalDescription

data class Source(
    val key: String,
    val displayName: String,
    val order: Int,
    val style: (isDark: Boolean) -> MessageItemStyle,
    val description: String = userSignalDescription,
    val signalDescription: String = userSignalDescription
)

fun Source.settingsTitle() = userSignalDescription