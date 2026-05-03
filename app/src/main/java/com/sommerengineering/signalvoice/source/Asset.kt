package com.sommerengineering.signalvoice.source

import com.sommerengineering.signalvoice.message.MessageItemStyle

data class Asset(
    val origin: String,
    val symbol: String,
    val displayName: String,
    val spokenName: String,
    val category: String,
    val exchange: String,
    val assetDescription: String,
    val signalDescription: String,
    val order: Int,
    val style: MessageItemStyle,
    val isPremium: Boolean
)