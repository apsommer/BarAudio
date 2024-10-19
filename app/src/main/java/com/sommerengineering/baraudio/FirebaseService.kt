package com.sommerengineering.baraudio

import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage

class FirebaseService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        logMessage("Firebase service, token refreshed: $token")
    }

    override fun onMessageReceived(message: RemoteMessage) {
        logMessage("Firebase service, message received: ${message.messageId}")

        message.notification?.let {
            logMessage("Firebase service, message notification body: ${it.body}")
        }

        if (message.data.isNotEmpty()) {
            logMessage("Firebase service, data payload: ${message.data}")
            // todo add new alert object to repo/firestore
        }
    }

    override fun onDeletedMessages() {
        logMessage("Firebase service, previous messages deleted")
    }
}

fun getFirebaseToken() {

    FirebaseMessaging.getInstance().token.addOnCompleteListener(
        OnCompleteListener { task ->

            if (!task.isSuccessful) {
                logMessage("Firebase token retrieval failed")
                return@OnCompleteListener
            }

            // extract registration token
            val token = task.result
            logMessage("Main activity started, firebase token: $token")
        }
    )
}