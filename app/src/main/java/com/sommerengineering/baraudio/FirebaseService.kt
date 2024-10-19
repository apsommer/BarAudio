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

fun testFirebaseDatabase() {

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

    // todo configure proguard for Alert pojo
    //  https://firebase.google.com/docs/database/android/start#proguard

    // todo complete launch checklist prior to production
    //  https://firebase.google.com/support/guides/launch-checklist

    // todo implement App Check via Google Play Integrity API, setup flow through console
    //  https://firebase.google.com/docs/app-check/android/play-integrity-provider?hl=en&authuser=0&_gl=1*4ksu49*_ga*NTE3MjAzMTkwLjE3Mjg1NTI5MDE.*_ga_CW55HF8NVT*MTcyOTM2MTg3NS4xOC4xLjE3MjkzNjQzODIuMC4wLjA.
}