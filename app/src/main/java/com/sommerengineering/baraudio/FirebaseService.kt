package com.sommerengineering.baraudio

import com.google.android.gms.tasks.OnCompleteListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessaging
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import java.util.Calendar

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

fun writeFirebaseDatabase() {

    // get reference to database
    val database = Firebase.database

    // write to database
    val key = database.getReference("timestamp")
    val timestamp = Calendar.getInstance().timeInMillis
    key.setValue("$timestamp")

    // read from database
    key.addValueEventListener(object : ValueEventListener {

        override fun onDataChange(snapshot: DataSnapshot) {
            val value = snapshot.getValue()
            logMessage("Firebase realtime database, $key: $value")
        }

        override fun onCancelled(error: DatabaseError) {
            logException(error.toException())
        }
    })

}