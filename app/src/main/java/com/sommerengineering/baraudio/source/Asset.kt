package com.sommerengineering.baraudio.source

data class Asset(
    val origin: String,
    val symbol: String,
    val displayName: String,
    val spokenName: String,
    val category: String,
    val exchange: String,
    val assetDescription: String,
    val streamDescription: String,
    val order: Int,
    val style: (isDark: Boolean) -> ItemStyle
)