package com.sommerengineering.signalvoice.subscription

sealed class Session {
    data class Authenticated(val uid: String) : Session()
    object Guest : Session()
}
