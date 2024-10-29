package com.sommerengineering.baraudio

import android.Manifest
import android.app.PendingIntent
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
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

        // extract attributes
        val timestamp = remoteMessage.data["timestamp"] ?: return
        val message = remoteMessage.data["message"] ?: return

        logMessage("FCM message received,")
        logMessage("    $timestamp: $message")

        // show notification
        showNotification(timestamp, message)

        // speak message
        tts.message = message
        tts.speakMessage()
    }

    private fun showNotification(
        timestamp: String,
        message: String
    ) {

        // confirm permission granted
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
            != PackageManager.PERMISSION_GRANTED) { return }

        // create pending intent to activity
        val intent = packageManager.getLaunchIntentForPackage(getString(R.string.package_name))
        val pendingIntent= PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)

        // configure options
        val builder = NotificationCompat.Builder(this, getString(R.string.notification_channel_id))
            .setSmallIcon(R.drawable.logo_square)
            .setContentTitle(timestamp)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle()
                .bigText(message))
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)
        
        // show notification
        NotificationManagerCompat.from(this).notify(
            timestamp.toLong().toInt(),
            builder.build())
    }
}
