package com.sommerengineering.baraudio.source

import androidx.compose.ui.graphics.Color
import com.sommerengineering.baraudio.R

val tradingViewSource = SignalSource(
    key = "tradingview",
    displayName = "TradingView",
    order = 0,
    style = { isDark ->
        MessageItemStyle(
            primary = Color(0xFF2962FF),
            accent  = Color(0xFF82B1FF),
            surface = if (isDark) Color(0xFF0B1A3A) else Color(0xFFE8F0FF),
            text    = if (isDark) Color(0xFFEAF1FF) else Color(0xFF0B1A3A),
            iconRes = if (isDark)
                R.drawable.tradingview_dark
            else
                R.drawable.tradingview_light
        )
    }
)

val trendSpiderSource = SignalSource(
    key = "trendspider",
    displayName = "TrendSpider",
    order = 1,
    style = { isDark ->
        MessageItemStyle(
            primary = Color(0xFF00C853),
            accent  = Color(0xFF69F0AE),
            surface = if (isDark) Color(0xFF002B12) else Color(0xFFE8FBEF),
            text    = if (isDark) Color(0xFFE8FBEF) else Color(0xFF002B12),
            iconRes = R.drawable.trendspider
        )
    }
)

val insomniaSource = SignalSource(
    key = "insomnia",
    displayName = "Insomnia",
    order = 2,
    style = { isDark ->
        MessageItemStyle(
            primary = Color(0xFFFF6D00),
            accent  = Color(0xFFFFAB40),
            surface = if (isDark) Color(0xFF2B1400) else Color(0xFFFFF1E6),
            text    = if (isDark) Color(0xFFFFF1E6) else Color(0xFF2B1400),
            iconRes = R.drawable.insomnia
        )
    }
)

val unknownSource = SignalSource(
    key = "unknown",
    displayName = "Unknown",
    order = 3,
    style = { isDark ->
        MessageItemStyle(
            primary = if (isDark) Color(0xFF8A8A8A) else Color(0xFF9E9E9E),
            accent  = if (isDark) Color(0xFFB0B0B0) else Color(0xFFC7C7C7),
            surface = if (isDark) Color(0xFF1C1C1C) else Color(0xFFF2F2F2),
            text    = if (isDark) Color(0xFFEAEAEA) else Color(0xFF1C1C1C),
            iconRes = R.drawable.webhook
        )
    }
)

val allSignalSources = listOf(tradingViewSource, trendSpiderSource, insomniaSource, unknownSource)
private val signalSourceMap = allSignalSources.associateBy { it.key }
fun resolveSignalSource(key: String) =
     signalSourceMap[key] ?: unknownSource