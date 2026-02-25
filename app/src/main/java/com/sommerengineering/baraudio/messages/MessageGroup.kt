package com.sommerengineering.baraudio.messages

import com.sommerengineering.baraudio.source.MessageOrigin

data class MessageGroup(
    val origin: MessageOrigin,
    val messages: List<Message>,
)