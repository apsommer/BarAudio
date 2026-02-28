package com.sommerengineering.baraudio.source

import androidx.compose.ui.graphics.Color
import com.sommerengineering.baraudio.R
import com.sommerengineering.baraudio.uitls.btcStream
import com.sommerengineering.baraudio.uitls.esStream
import com.sommerengineering.baraudio.uitls.gcStream
import com.sommerengineering.baraudio.uitls.nqStream

val nqAsset = Asset(
    origin = nqStream,
    symbol = "NQ",
    displayName = "Nasdaq-100",
    spokenName = "Nasdaq",
    category = "Equity Index",
    exchange = "CME",
    assetDescription = "CME · Equity Index · E-mini Nasdaq-100 Futures",
    signalDescription = "High velocity momentum",
    order = 0,
    style = { isDark ->
        if (isDark) {
            ItemStyle(
                primary = Color(0xFF9A84FF),
                accent = Color(0xFFC2B5FF),
                surface = Color(0xFF15122A),
                text = Color(0xFFEAE6FF),
                iconRes = R.drawable.nq)
        } else {
            ItemStyle(
                primary = Color(0xFF7B61FF),
                accent = Color(0xFF9A84FF),
                surface = Color(0xFFF3F0FF),
                text = Color(0xFF1F1B2E),
                iconRes = R.drawable.nq) }})

val esAsset = Asset(
    origin = esStream,
    symbol = "ES",
    displayName = "S&P 500",
    spokenName = "S and P",
    category = "Equity Index",
    exchange = "CME",
    assetDescription = "CME · Equity Index · E-mini S&P 500 Futures",
    signalDescription = "Macro impulse swings",
    order = 1,
    style = { isDark ->
        if (isDark) {
            ItemStyle(
                primary = Color(0xFF69A6FF),
                accent = Color(0xFF9CC4FF),
                surface = Color(0xFF0D1A2B),
                text = Color(0xFFE6F0FF),
                iconRes = R.drawable.es)
        } else {
            ItemStyle(
                primary = Color(0xFF3A86FF),
                accent = Color(0xFF69A6FF),
                surface = Color(0xFFEEF4FF),
                text = Color(0xFF0F1C2E),
                iconRes = R.drawable.es) }})

val btcAsset = Asset(
    origin = btcStream,
    symbol = "BTC",
    displayName = "Bitcoin",
    spokenName = "Bitcoin",
    category = "Cryptocurrency",
    exchange = "CME",
    assetDescription = "CME · Cryptocurrency · Bitcoin Futures",
    signalDescription = "Volatility breakouts",
    order = 2,
    style = { isDark ->
        if (isDark) {
            ItemStyle(
                primary = Color(0xFFFFB454),
                accent = Color(0xFFFFD089),
                surface = Color(0xFF2A1B00),
                text = Color(0xFFFFF3E0),
                iconRes = R.drawable.btc)
        } else {
            ItemStyle(
                primary = Color(0xFFF7931A),
                accent = Color(0xFFFFB454),
                surface = Color(0xFFFFF4E5),
                text = Color(0xFF2A1B00),
                iconRes = R.drawable.btc) }})

val gcAsset = Asset(
    origin = gcStream,
    symbol = "GC",
    displayName = "Gold",
    spokenName = "Gold",
    category = "Metals",
    exchange = "COMEX",
    assetDescription = "COMEX · Metals · Gold Futures",
    signalDescription = "Gold description ...",
    order = 3,
    style = { isDark ->
        if (isDark) {
            ItemStyle(
                primary = Color(0xFFE6C96A),
                accent = Color(0xFFF4DE9A),
                surface = Color(0xFF2B2400),
                text = Color(0xFFFFF9E6),
                iconRes = R.drawable.gc)
        } else {
            ItemStyle(
                primary = Color(0xFFD4AF37),
                accent = Color(0xFFE6C96A),
                surface = Color(0xFFFFF8E1),
                text = Color(0xFF2B2400),
                iconRes = R.drawable.gc) }})

fun Asset.settingsTitle() = "$displayName ($symbol)"

val allAssets = listOf(nqAsset, esAsset, btcAsset, gcAsset)
val assetMap = allAssets.associateBy { it.origin }
fun resolveAsset(stream: String) =
    assetMap[stream] ?: error("Unknown asset for stream: $stream")
