package com.sommerengineering.baraudio

import com.google.firebase.messaging.FirebaseMessagingService

class FirebaseService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
    }
}