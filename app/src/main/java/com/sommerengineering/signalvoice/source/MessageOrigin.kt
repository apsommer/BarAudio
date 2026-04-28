package com.sommerengineering.signalvoice.source

import com.sommerengineering.signalvoice.message.MessageItemStyle

sealed interface MessageOrigin {

    val key: String
    val displayName: String
    val description: String // group header description
    val signalDescription: String // settings description
    val order: Int
    val style: MessageItemStyle

    data class BroadcastStream(val asset: Asset) : MessageOrigin {
        override val key = asset.origin
        override val displayName = asset.displayName
        override val description = asset.assetDescription
        override val signalDescription = asset.signalDescription
        override val order = asset.order
        override val style = asset.style
    }

    data class UserSignal(val source: Source) : MessageOrigin {
        override val key = source.key
        override val displayName = source.displayName
        override val description = source.description
        override val signalDescription = source.signalDescription
        override val order = source.order + 100 // always after assets
        override val style = source.style
    }
}

fun resolveMessageOrigin(message: Message): MessageOrigin {

    val stream = message.stream
    val source = message.source ?: "unknown"

    if (stream != null) {
        return MessageOrigin.BroadcastStream(resolveAsset(stream))
    }
    return MessageOrigin.UserSignal(resolveSignalSource(source))
}
