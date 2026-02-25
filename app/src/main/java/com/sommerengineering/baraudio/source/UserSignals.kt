package com.sommerengineering.baraudio.source

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.sommerengineering.baraudio.R

enum class SignalSourceX {
    TradingView,
    TrendSpider,
    Insomnia,
    Unknown
}

// https://www.tradingview.com/support/solutions/43000529348-about-webhooks/
// https://help.trendspider.com/kb/alerts/webhooks
fun detectSource(sourceIp: String): SignalSourceX {
    return when(sourceIp) {
        in listOf(
            "52.89.214.238",
            "34.212.75.30",
            "54.218.53.128",
            "52.32.178.7"
        ) -> {
            SignalSourceX.TradingView
        }

        "3.12.143.24" -> {
            SignalSourceX.TrendSpider
        }

        "84.123.224.196" -> {
            SignalSourceX.Insomnia
        }

        else -> {
            SignalSourceX.Unknown
        }
    }
}

@Composable
fun resolveUserSignalStyle(
    source: SignalSourceX,
    isDark: Boolean): MessageItemStyle {

    return when (source) {

        SignalSourceX.TradingView -> MessageItemStyle(
            primary = Color(0xFF2962FF),
            accent = Color(0xFF82B1FF),
            surface = if (isDark) Color(0xFF0B1A3A) else Color(0xFFE8F0FF),
            text = MaterialTheme.colorScheme.onSurface,
            iconRes = if (isDark) R.drawable.tradingview_dark else R.drawable.tradingview_light
        )

        SignalSourceX.TrendSpider -> MessageItemStyle(
            primary = Color(0xFF00C853),
            accent = Color(0xFF69F0AE),
            surface = if (isDark) Color(0xFF002B12) else Color(0xFFE8FBEF),
            text = MaterialTheme.colorScheme.onSurface,
            iconRes = R.drawable.trendspider
        )

        SignalSourceX.Insomnia -> MessageItemStyle(
            primary = Color(0xFFFF6D00),
            accent = Color(0xFFFFAB40),
            surface = if (isDark) Color(0xFF2B1400) else Color(0xFFFFF1E6),
            text = MaterialTheme.colorScheme.onSurface,
            iconRes = R.drawable.insomnia
        )

        SignalSourceX.Unknown -> MessageItemStyle(
            primary = MaterialTheme.colorScheme.outline,
            accent = MaterialTheme.colorScheme.outlineVariant,
            surface = MaterialTheme.colorScheme.surfaceContainer,
            text = MaterialTheme.colorScheme.onSurface,
            iconRes = R.drawable.webhook
        )
    }
}