package com.sommerengineering.baraudio.source

import androidx.compose.ui.graphics.Color
import com.sommerengineering.baraudio.R
import com.sommerengineering.baraudio.message.MessageItemStyle

val tradingViewSource = Source(
    key = "tradingview",
    displayName = "TradingView",
    order = 0,
    style = MessageItemStyle(
        primary = Color(0xFF2962FF),
        accent = Color(0xFF82B1FF),
        surface = Color(0xFF0B1A3A),
        text = Color(0xFFEAF1FF),
        iconRes = R.drawable.tradingview_light))

val trendSpiderSource = Source(
    key = "trendspider",
    displayName = "TrendSpider",
    order = 1,
    style = MessageItemStyle(
        primary = Color(0xFF00C853),
        accent = Color(0xFF69F0AE),
        surface = Color(0xFF002B12),
        text = Color(0xFFE8FBEF),
        iconRes = R.drawable.trendspider))

val insomniaSource = Source(
    key = "insomnia",
    displayName = "Insomnia",
    order = 2,
    style = MessageItemStyle(
        primary = Color(0xFF4000BF),
        accent = Color(0xFF7C4DFF),
        surface = Color(0xFF1B0D3A),
        text = Color(0xFFF0E9FF),
        iconRes = R.drawable.insomnia))

val unknownSource = Source(
    key = "unknown",
    displayName = "Unknown",
    order = 3,
    style = MessageItemStyle(
            primary = Color(0xFF8A8A8A),
            accent = Color(0xFFB0B0B0),
            surface = Color(0xFF1C1C1C),
            text = Color(0xFFEAEAEA),
            iconRes = R.drawable.webhook))

val allSignalSources = listOf(tradingViewSource, trendSpiderSource, insomniaSource, unknownSource)
private val signalSourceMap = allSignalSources.associateBy { it.key }
fun resolveSignalSource(key: String) =
     signalSourceMap[key] ?: unknownSource