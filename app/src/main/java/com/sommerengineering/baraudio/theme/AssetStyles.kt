package com.sommerengineering.baraudio.theme

import androidx.compose.ui.graphics.Color
import com.sommerengineering.baraudio.R

object AssetStyles {

    fun nq(isDark: Boolean) = if (isDark) {
        AssetStyle(
            primary = Color(0xFF9A84FF),
            accent  = Color(0xFFC2B5FF),
            surface = Color(0xFF15122A),
            text    = Color(0xFFEAE6FF),
            iconRes = R.drawable.ic_nq
        )
    } else {
        AssetStyle(
            primary = Color(0xFF7B61FF),
            accent  = Color(0xFF9A84FF),
            surface = Color(0xFFF3F0FF),
            text    = Color(0xFF1F1B2E),
            iconRes = R.drawable.ic_nq
        )
    }

    fun es(isDark: Boolean) = if (isDark) {
        AssetStyle(
            primary = Color(0xFF69A6FF),
            accent  = Color(0xFF9CC4FF),
            surface = Color(0xFF0D1A2B),
            text    = Color(0xFFE6F0FF),
            iconRes = R.drawable.ic_es
        )
    } else {
        AssetStyle(
            primary = Color(0xFF3A86FF),
            accent  = Color(0xFF69A6FF),
            surface = Color(0xFFEEF4FF),
            text    = Color(0xFF0F1C2E),
            iconRes = R.drawable.ic_es
        )
    }

    fun btc(isDark: Boolean) = if (isDark) {
        AssetStyle(
            primary = Color(0xFFFFB454),
            accent  = Color(0xFFFFD089),
            surface = Color(0xFF2A1B00),
            text    = Color(0xFFFFF3E0),
            iconRes = R.drawable.ic_btc
        )
    } else {
        AssetStyle(
            primary = Color(0xFFF7931A),
            accent = Color(0xFFFFB454),
            surface = Color(0xFFFFF4E5),
            text = Color(0xFF2A1B00),
            iconRes = R.drawable.ic_btc
        )
    }

    fun gc(isDark: Boolean) = if (isDark) {
        AssetStyle(
            primary = Color(0xFFE6C96A),
            accent  = Color(0xFFF4DE9A),
            surface = Color(0xFF2B2400),
            text    = Color(0xFFFFF9E6),
            iconRes = R.drawable.ic_gc
        )
    } else {
        AssetStyle(
            primary = Color(0xFFD4AF37),
            accent  = Color(0xFFE6C96A),
            surface = Color(0xFFFFF8E1),
            text    = Color(0xFF2B2400),
            iconRes = R.drawable.ic_gc
        )
    }
}