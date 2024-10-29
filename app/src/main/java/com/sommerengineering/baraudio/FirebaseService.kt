package com.sommerengineering.baraudio

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import org.koin.android.ext.android.get
import org.koin.java.KoinJavaComponent.inject

class FirebaseService : FirebaseMessagingService() {

    private val tts: TextToSpeechImpl = get()

    override fun onNewToken(token: String) { writeNewUserToDatabase(token) }
    override fun onMessageReceived(remoteMessage: RemoteMessage) { handleMessage(remoteMessage) }

    private fun writeNewUserToDatabase(token: String) {

        // get user id
        val firebaseAuth: FirebaseAuth by inject(FirebaseAuth::class.java)
        val uid = firebaseAuth.currentUser?.uid ?: return

        // write new user/token to database
        Firebase.database(databaseUrl)
            .getReference(users)
            .child(uid)
            .setValue(token)

        logMessage("New user:token written to database")
    }

    private fun handleMessage(remoteMessage: RemoteMessage) {

        val notification = remoteMessage.notification
        val message = remoteMessage.data["message"] ?: return

        logMessage("FCM message received,")
        logMessage("    notification: ${notification?.title}, ${notification?.body}")
        logMessage("    data: $message")

        // todo display notification
        //  group all together, click opens existing app instance, or launches new one

        tts.message = message
        tts.speakMessage()
    }
}
