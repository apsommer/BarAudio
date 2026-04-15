package com.sommerengineering.baraudio.source

import androidx.compose.ui.graphics.Color
import com.sommerengineering.baraudio.R
import com.sommerengineering.baraudio.message.MessageItemStyle
import com.sommerengineering.baraudio.uitls.btcStream
import com.sommerengineering.baraudio.uitls.esStream
import com.sommerengineering.baraudio.uitls.gcStream
import com.sommerengineering.baraudio.uitls.nqStream
import com.sommerengineering.baraudio.uitls.siStream
import com.sommerengineering.baraudio.uitls.znStream

val znAsset = Asset(
    origin = znStream,
    symbol = "ZN",
    displayName = "10Y Treasury",
    spokenName = "ten year treasury",
    category = "Rates",
    exchange = "CBOT",
    assetDescription = "CBOT · Rates · 10-Year U.S. Treasury Note Futures",
    signalDescription = "Macro rate shifts",
    order = 0,
    style = { isDark ->
        if (isDark) {
            MessageItemStyle(
                primary = Color(0xFF2FA38A),
                accent = Color(0xFF6FC9B5),
                surface = Color(0xFF0E2A26),
                text = Color(0xFFE6FFFA),
                iconRes = null,
                iconText = "10Y"
            )
        } else {
            MessageItemStyle(
                primary = Color(0xFF1F7A6B),
                accent = Color(0xFF2FA38A),
                surface = Color(0xFFE8F5F2),
                text = Color(0xFF0F2A26),
                iconRes = null,
                iconText = "10Y"
            )
        }
    }
)

val nqAsset = Asset(
    origin = nqStream,
    symbol = "NQ",
    displayName = "Nasdaq 100",
    spokenName = "Nasdaq one hundred",
    category = "Equity Index",
    exchange = "CME",
    assetDescription = "CME · Equity Index · E-mini Nasdaq 100 Futures",
    signalDescription = "High velocity momentum",
    order = 1,
    style = { isDark ->
        if (isDark) {
            MessageItemStyle(
                primary = Color(0xFF9A84FF),
                accent = Color(0xFFC2B5FF),
                surface = Color(0xFF15122A),
                text = Color(0xFFEAE6FF),
                iconRes = null,
                iconText = "100"
            )
        } else {
            MessageItemStyle(
                primary = Color(0xFF7B61FF),
                accent = Color(0xFF9A84FF),
                surface = Color(0xFFF3F0FF),
                text = Color(0xFF1F1B2E),
                iconRes = null,
                iconText = "100"
            )
        }})

val btcAsset = Asset(
    origin = btcStream,
    symbol = "BTC",
    displayName = "Bitcoin",
    spokenName = "Bitcoin",
    category = "Cryptocurrency",
    exchange = "Binance",
    assetDescription = "Binance · Cryptocurrency · Bitcoin USDT (Spot)",
    signalDescription = "Volatility breakouts",
    order = 2,
    style = { isDark ->
        if (isDark) {
            MessageItemStyle(
                primary = Color(0xFFF7931A),
                accent = Color(0xFFFFB347),
                surface = Color(0xFF2A1A0A),
                text = Color(0xFFFFF4E6),
                iconRes = R.drawable.btc
            )
        } else {
            MessageItemStyle(
                primary = Color(0xFFF7931A),
                accent = Color(0xFFFFA94D),
                surface = Color(0xFFFFF4E6),
                text = Color(0xFF2A1A0A),
                iconRes = R.drawable.btc
            )
        }
    }
)

// premium /////////////////////////////////////////////////////////////////////////////////////////

val esAsset = Asset(
    origin = esStream,
    symbol = "ES",
    displayName = "S&P 500",
    spokenName = "S and P five hundred",
    category = "Equity Index",
    exchange = "CME",
    assetDescription = "CME · Equity Index · E-mini S&P 500 Futures",
    signalDescription = "Balanced trend structure",
    order = 3,
    style = { isDark ->
        if (isDark) {
            MessageItemStyle(
                primary = Color(0xFF69A6FF),
                accent = Color(0xFF9CC4FF),
                surface = Color(0xFF0D1A2B),
                text = Color(0xFFE6F0FF),
                iconRes = null,
                iconText = "500"
            )
        } else {
            MessageItemStyle(
                primary = Color(0xFF3A86FF),
                accent = Color(0xFF69A6FF),
                surface = Color(0xFFEEF4FF),
                text = Color(0xFF0F1C2E),
                iconRes = null,
                iconText = "500"
            )
        }})

val gcAsset = Asset(
    origin = gcStream,
    symbol = "GC",
    displayName = "Gold",
    spokenName = "Gold",
    category = "Metals",
    exchange = "COMEX",
    assetDescription = "COMEX · Metals · Gold Futures",
    signalDescription = "Macro impulse swings",
    order = 4,
    style = { isDark ->
        if (isDark) {
            MessageItemStyle(
                primary = Color(0xFFE6C96A),
                accent = Color(0xFFF4DE9A),
                surface = Color(0xFF2B2400),
                text = Color(0xFFFFF9E6),
                iconRes = R.drawable.gc
            )
        } else {
            MessageItemStyle(
                primary = Color(0xFFD4AF37),
                accent = Color(0xFFE6C96A),
                surface = Color(0xFFFFF8E1),
                text = Color(0xFF2B2400),
                iconRes = R.drawable.gc
            )
        }})

val siAsset = Asset(
    origin = siStream,
    symbol = "SI",
    displayName = "Silver",
    spokenName = "Silver",
    category = "Metals",
    exchange = "COMEX",
    assetDescription = "COMEX · Metals · Silver Futures",
    signalDescription = "Cascade repair structure",
    order = 5,
    style = { isDark ->
        if (isDark) {
            MessageItemStyle(
                primary = Color(0xFFE6EEF5),
                accent = Color(0xFFFFFFFF),
                surface = Color(0xFF0F1418),
                text = Color(0xFFF5FAFF),
                iconRes = R.drawable.si)
        } else {
            MessageItemStyle(
                primary = Color(0xFFDCE6EE),
                accent = Color(0xFFF8FCFF),
                surface = Color(0xFFF6F9FC),
                text = Color(0xFF111417),
                iconRes = R.drawable.si) } })

fun Asset.settingsTitle() = "$displayName ($symbol)"

val allAssets = listOf(znAsset, nqAsset, btcAsset, esAsset, gcAsset, siAsset)
val assetMap = allAssets.associateBy { it.origin }
fun resolveAsset(stream: String) =
    assetMap[stream] ?: error("Unknown asset for stream: $stream")
