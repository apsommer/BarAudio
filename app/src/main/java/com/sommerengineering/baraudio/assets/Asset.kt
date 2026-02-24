package com.sommerengineering.baraudio.assets

data class Asset(
    val origin: String,
    val symbol: String,
    val displayName: String,
    val category: String,
    val exchange: String,
    val description: String,
    val order: Int
)