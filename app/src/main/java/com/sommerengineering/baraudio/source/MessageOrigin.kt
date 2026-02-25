package com.sommerengineering.baraudio.source

import com.sommerengineering.baraudio.messages.Message

sealed interface MessageOrigin {

    val key: String
    val displayName: String
    val description: String
    val order: Int
    fun style(isDark: Boolean): MessageItemStyle

    data class Stream(val asset: Asset) : MessageOrigin {
        override val key = asset.origin
        override val displayName = asset.displayName
        override val description = asset.description
        override val order = asset.order
        override fun style(isDark: Boolean) = asset.style(isDark)
    }

    data class UserSignal(val source: SignalSource) : MessageOrigin {
        override val key = source.key
        override val displayName = source.displayName
        override val description = "User signal"
        override val order = source.order + 100 // always after assets
        override fun style(isDark: Boolean) = source.style(isDark)
    }
}

fun resolveMessageOrigin(message: Message): MessageOrigin {

    val stream = message.stream
    val source = message.source ?: "unknown"

    if (stream != null) { return MessageOrigin.Stream(resolveAsset(stream)) }
    if (source != null) { return MessageOrigin.UserSignal(resolveSignalSource(source)) }

    error("Unknown origin for message: $message")
}
