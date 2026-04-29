package com.sommerengineering.signalvoice.subscription

enum class UserState {
    GUEST,
    AUTHENTICATED_FREE,
    AUTHENTICATED_SUBSCRIBED
}

enum class StreamTier {
    FREE,
    PREMIUM
}

enum class AccessState {
    AVAILABLE,
    LOCKED
}

fun resolveAccessState(
    userState: UserState,
    streamTier: StreamTier
): AccessState {
    return when {
        streamTier == StreamTier.FREE -> AccessState.AVAILABLE
        userState == UserState.AUTHENTICATED_SUBSCRIBED -> AccessState.AVAILABLE
        else -> AccessState.LOCKED
    }
}