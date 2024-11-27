package com.sommerengineering.baraudio.messages

import com.sommerengineering.baraudio.R
import com.sommerengineering.baraudio.insomnia

data class Message(
    var timestamp: String,
    var message: String,
    var originImageId: Int?
)

fun getOriginImageId(
    origin: String): Int? =

    when (origin) {
        insomnia -> R.drawable.insomnia
        in tradingviewWhitelistIps -> R.drawable.tradingview
        trendspiderWhitelistIp -> R.drawable.trendspider
        error -> R.drawable.error
        else -> null
    }

// https://www.tradingview.com/support/solutions/43000529348-about-webhooks/
val tradingviewWhitelistIps = listOf(
    "52.89.214.238",
    "34.212.75.30",
    "54.218.53.128",
    "52.32.178.7")

// https://help.trendspider.com/kb/alerts/webhooks
const val trendspiderWhitelistIp = "3.12.143.24"
const val error = "error"