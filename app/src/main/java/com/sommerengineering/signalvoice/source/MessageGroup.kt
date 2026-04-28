package com.sommerengineering.signalvoice.source

data class MessageGroup(
    val origin: MessageOrigin,
    val messages: List<Message>,
)