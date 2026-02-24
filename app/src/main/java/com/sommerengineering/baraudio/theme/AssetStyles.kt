package com.sommerengineering.baraudio.theme

import androidx.compose.material3.MaterialTheme
import androidx.compose.runtime.Composable
import androidx.compose.ui.graphics.Color
import com.sommerengineering.baraudio.R
import com.sommerengineering.baraudio.uitls.btcStream
import com.sommerengineering.baraudio.uitls.esStream
import com.sommerengineering.baraudio.uitls.gcStream
import com.sommerengineering.baraudio.uitls.nqStream

object AssetStyles {

    fun nq(isDark: Boolean) =
        if (isDark) {
            AssetStyle(
                primary = Color(0xFF9A84FF),
                accent  = Color(0xFFC2B5FF),
                surface = Color(0xFF15122A),
                text    = Color(0xFFEAE6FF),
                iconRes = R.drawable.nq)
        } else {
            AssetStyle(
                primary = Color(0xFF7B61FF),
                accent  = Color(0xFF9A84FF),
                surface = Color(0xFFF3F0FF),
                text    = Color(0xFF1F1B2E),
                iconRes = R.drawable.nq)
        }

    fun es(isDark: Boolean) =
        if (isDark) {
            AssetStyle(
                primary = Color(0xFF69A6FF),
                accent  = Color(0xFF9CC4FF),
                surface = Color(0xFF0D1A2B),
                text    = Color(0xFFE6F0FF),
                iconRes = R.drawable.es)
        } else {
            AssetStyle(
                primary = Color(0xFF3A86FF),
                accent  = Color(0xFF69A6FF),
                surface = Color(0xFFEEF4FF),
                text    = Color(0xFF0F1C2E),
                iconRes = R.drawable.es)
        }

    fun btc(isDark: Boolean) =
        if (isDark) {
            AssetStyle(
                primary = Color(0xFFFFB454),
                accent  = Color(0xFFFFD089),
                surface = Color(0xFF2A1B00),
                text    = Color(0xFFFFF3E0),
                iconRes = R.drawable.btc)
        } else {
            AssetStyle(
                primary = Color(0xFFF7931A),
                accent = Color(0xFFFFB454),
                surface = Color(0xFFFFF4E5),
                text = Color(0xFF2A1B00),
                iconRes = R.drawable.btc)
        }

    fun gc(isDark: Boolean) =
        if (isDark) {
            AssetStyle(
                primary = Color(0xFFE6C96A),
                accent  = Color(0xFFF4DE9A),
                surface = Color(0xFF2B2400),
                text    = Color(0xFFFFF9E6),
                iconRes = R.drawable.gc)
        } else {
            AssetStyle(
                primary = Color(0xFFD4AF37),
                accent  = Color(0xFFE6C96A),
                surface = Color(0xFFFFF8E1),
                text    = Color(0xFF2B2400),
                iconRes = R.drawable.gc)
        }
}

@Composable
fun resolveAssetStyle(
    origin: String,
    isDark: Boolean)= when (origin) {

    // streams
    nqStream -> AssetStyles.nq(isDark)
    esStream -> AssetStyles.es(isDark)
    btcStream -> AssetStyles.btc(isDark)
    gcStream -> AssetStyles.gc(isDark)

    // todo user specific
    else -> AssetStyle(
        primary = MaterialTheme.colorScheme.outline,
        accent  = MaterialTheme.colorScheme.outlineVariant,
        surface = MaterialTheme.colorScheme.surfaceContainer,
        text    = MaterialTheme.colorScheme.onSurface,
        iconRes = R.drawable.webhook)
//        in tradingview -> {
//            if (isDarkMode) R.drawable.tradingview_light
//            else R.drawable.tradingview_dark
//        }
//        trendspider -> R.drawable.trendspider
//        insomnia -> R.drawable.insomnia
//        parsingErrorOrigin -> R.drawable.error
//        else -> R.drawable.webhook
}