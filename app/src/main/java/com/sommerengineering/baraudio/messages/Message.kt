package com.sommerengineering.baraudio.messages

data class Message(
    var timestamp: String,
    var message: String,
    var origin: String
)

// https://www.tradingview.com/support/solutions/43000529348-about-webhooks/
val tradingviewWhitelistIps = listOf(
    "52.89.214.238",
    "34.212.75.30",
    "54.218.53.128",
    "52.32.178.7")

// https://help.trendspider.com/kb/alerts/webhooks
const val trendspiderWhitelistIp = "3.12.143.24"

const val error = "error"
