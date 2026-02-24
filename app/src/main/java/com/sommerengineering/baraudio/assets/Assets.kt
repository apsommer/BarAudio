package com.sommerengineering.baraudio.assets

import com.sommerengineering.baraudio.uitls.btcStream
import com.sommerengineering.baraudio.uitls.esStream
import com.sommerengineering.baraudio.uitls.gcStream
import com.sommerengineering.baraudio.uitls.nqStream

val nqAsset = Asset(
    origin = nqStream,
    symbol = "NQ",
    displayName = "Nasdaq",
    description = "CME · Chicago · E-Mini Nasdaq Futures")

val esAsset = Asset(
    origin = esStream,
    symbol = "ES",
    displayName = "S&P 500",
    description = "CME · Chicago · E-Mini S&P Futures")

val btcAsset = Asset(
    origin = btcStream,
    symbol = "BTC",
    displayName = "Bitcoin",
    description = "CME · Crypto · Bitcoin Futures")

val gcAsset = Asset(
    origin = gcStream,
    symbol = "GC",
    displayName = "Gold",
    description = "COMEX · NYC · Mini Gold Futures")

val assetMap = mapOf(
    nqAsset.origin to nqAsset,
    esAsset.origin to esAsset,
    btcAsset.origin to btcAsset,
    gcAsset.origin to gcAsset)

fun resolveAsset(origin: String): Asset =
    assetMap[origin] ?: error("Unknown asset origin: $origin")