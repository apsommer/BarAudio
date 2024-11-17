package com.sommerengineering.baraudio.messages

import com.sommerengineering.baraudio.R
import com.sommerengineering.baraudio.insomnia

data class Message(
    var timestamp: String,
    var message: String,
    var originImageId: Int?
)

fun getOriginImageId(origin: String): Int? {

    if (origin == insomnia) return R.drawable.insomnia
    tradingviewWhitelistIps.forEach { if (origin == it) return R.drawable.tradingview }
    if (origin.equals(trendspiderWhitelistIp)) return R.drawable.trendspider

    return null
}

// https://www.tradingview.com/support/solutions/43000529348-about-webhooks/
val tradingviewWhitelistIps = listOf(
    "52.89.214.238",
    "34.212.75.30",
    "54.218.53.128",
    "52.32.178.7")

// https://help.trendspider.com/kb/alerts/webhooks
val trendspiderWhitelistIp = "3.12.143.24"