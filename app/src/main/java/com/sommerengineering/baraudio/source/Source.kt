package com.sommerengineering.baraudio.source

data class Source(
    val key: String,
    val displayName: String,
    val order: Int,
    val style: (isDark: Boolean) -> ItemStyle
)
