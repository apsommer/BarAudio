package com.sommerengineering.baraudio.source

data class MessageGroup(
    val origin: MessageOrigin,
    val messages: List<Message>,
)