package com.sommerengineering.baraudio.assets

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
    description = "CME · Equity Index · E-mini Nasdaq-100 Futures",
    order = 0
)

val esAsset = Asset(
    origin = esStream,
    symbol = "ES",
    displayName = "S&P 500",
    spokenName = "S and P",
    category = "Equity Index",
    exchange = "CME",
    description = "CME · Equity Index · E-mini S&P 500 Futures",
    order = 1
)

val btcAsset = Asset(
    origin = btcStream,
    symbol = "BTC",
    displayName = "Bitcoin",
    spokenName = "Bitcoin",
    category = "Cryptocurrency",
    exchange = "CME",
    description = "CME · Cryptocurrency · Bitcoin Futures",
    order = 2
)

val gcAsset = Asset(
    origin = gcStream,
    symbol = "GC",
    displayName = "Gold",
    spokenName = "Gold",
    category = "Metals",
    exchange = "COMEX",
    description = "COMEX · Metals · Gold Futures",
    order = 3
)

val allAssets = listOf(nqAsset, esAsset, btcAsset, gcAsset)
val assetMap = allAssets.associateBy { it.origin }
fun resolveAsset(origin: String): Asset =
    assetMap[origin] ?: error("Unknown asset origin: $origin")