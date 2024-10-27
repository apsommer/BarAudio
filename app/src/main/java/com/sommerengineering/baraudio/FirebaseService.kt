package com.sommerengineering.baraudio

import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import org.koin.android.ext.android.get
import org.koin.java.KoinJavaComponent.inject

class FirebaseService : FirebaseMessagingService() {

    private val tts: TextToSpeechImpl = get()

    override fun onNewToken(token: String) { writeNewUserToDatabase(token) }
    override fun onMessageReceived(message: RemoteMessage) { announceMessage(message) }

    private fun writeNewUserToDatabase(token: String) {

        // get user id
        val firebaseAuth: FirebaseAuth by inject(FirebaseAuth::class.java)
        val uid = firebaseAuth.currentUser?.uid ?: return

        // write new user/token to database
        Firebase.database(databaseUrl)
            .getReference(users)
            .child(uid)
            .setValue(token)

        logMessage("New user/token written to database")
    }

    private fun announceMessage(remoteMessage: RemoteMessage) {

        val message = remoteMessage.data["message"] ?: return
        logMessage("onMessageReceived: $message")
        tts.announceMessage(message)
    }
}





fun listenToDatabaseWrites() {

    // get user id
    val firebaseAuth: FirebaseAuth by inject(FirebaseAuth::class.java)
    val uid = firebaseAuth.currentUser?.uid ?: return

    // get reference to database
    val urlString = "https://com-sommerengineering-baraudio.firebaseio.com/"
    val db = Firebase.database(urlString)
    val messagesKey = db.getReference("messages").child(uid)

    // listen to new message database writes
    messagesKey.addValueEventListener(object : ValueEventListener {

        override fun onDataChange(snapshot: DataSnapshot) {
            val value = snapshot.getValue()
            logMessage("Firebase realtime database, onDataChange: $messagesKey: $value")
        }

        override fun onCancelled(error: DatabaseError) {
            logException(error.toException())
        }
    })

    // todo configure proguard for Alert pojo
    //  https://firebase.google.com/docs/database/android/start#proguard

    // todo complete launch checklist prior to production
    //  https://firebase.google.com/support/guides/launch-checklist

    // todo implement App Check via Google Play Integrity API, setup flow through firebase console
    //  https://firebase.google.com/docs/app-check/android/play-integrity-provider?hl=en&authuser=0&_gl=1*4ksu49*_ga*NTE3MjAzMTkwLjE3Mjg1NTI5MDE.*_ga_CW55HF8NVT*MTcyOTM2MTg3NS4xOC4xLjE3MjkzNjQzODIuMC4wLjA.
}
