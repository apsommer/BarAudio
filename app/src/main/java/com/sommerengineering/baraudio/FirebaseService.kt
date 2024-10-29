package com.sommerengineering.baraudio

import android.Manifest
import android.app.NotificationManager
import android.app.PendingIntent
import android.content.Intent
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.sommerengineering.baraudio.alerts.AlertsScreen
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

        // create pending intent to activity
        val intent = Intent(this, MainActivity::class.java)
            .apply { flags = Intent.FLAG_ACTIVITY_NEW_TASK or Intent.FLAG_ACTIVITY_CLEAR_TASK }
        val pendingIntent: PendingIntent = PendingIntent
            .getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)

        val builder = NotificationCompat.Builder(this, getString(R.string.notification_channel_id))
            .setSmallIcon(R.drawable.logo_square)
            .setContentTitle(timestamp)
            .setContentText(message)
            .setStyle(NotificationCompat.BigTextStyle()
                .bigText(message))
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        // confirm permission granted
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
            != PackageManager.PERMISSION_GRANTED) {

            // todo is it even possible for user to reject manifest permission?
            //  if so, show ui here explain it's a requirement
            return
        }

        // show notification
        NotificationManagerCompat.from(this).notify(
            timestamp.toLong().toInt(),
            builder.build())
    }
}
