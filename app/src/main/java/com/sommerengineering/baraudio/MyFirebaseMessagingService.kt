package com.sommerengineering.baraudio

import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingService

class FirebaseService : FirebaseMessagingService() {

    override fun onNewToken(token: String) {
        super.onNewToken(token)
        logMessage("Firebase token refreshed: $token")
    }
}

fun getFirebaseToken() {

    FirebaseMessaging.getInstance().token.addOnCompleteListener(
        OnCompleteListener { task ->

            if (!task.isSuccessful) {
                logException("Firebase token retrieval failed")
                return@OnCompleteListener
            }

            // extract registration token
            val token = task.result
            logMessage("Firebase token: $token")
        }
    )
}