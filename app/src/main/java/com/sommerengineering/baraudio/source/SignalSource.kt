package com.sommerengineering.baraudio.source

data class SignalSource(
    val key: String,
    val displayName: String,
    val order: Int,
    val style: (isDark: Boolean) -> MessageItemStyle
)
