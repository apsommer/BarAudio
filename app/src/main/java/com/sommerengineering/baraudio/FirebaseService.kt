package com.sommerengineering.baraudio

import android.Manifest
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import org.koin.android.ext.android.get

class FirebaseService: FirebaseMessagingService() {

    private val tts: TextToSpeechImpl = get()

    override fun onNewToken(newToken: String) {

        token = newToken

        writeToDataStore(
            applicationContext,
            tokenKey,
            newToken)

        logMessage("New token: $token")
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {

        // extract attributes
        val timestamp = remoteMessage.data[timestamp] ?: return
        val message = remoteMessage.data[message] ?: return

        // show notification
        showNotification(timestamp, message)

        // only speak if user signed-in, and app open in background or foreground
        if (Firebase.auth.currentUser == null || !isAppOpen) { return }

        // speak message
        tts.speak(timestamp, message)
    }

    private fun showNotification(
        timestamp: String,
        message: String) {

        // todo check that notifications have appropriate settings:
        //  importance, sound, etc at minimum levels, else show ui saying it's required
        //  also put link to system settings somewhere appropriate
        //  https://developer.android.com/develop/ui/views/notifications/channels#UpdateChannel

        // confirm permission granted
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
            != PackageManager.PERMISSION_GRANTED) { return }

        // create pending intent to activity
        val intent = Intent(this, MainActivity::class.java)
            .putExtra(isLaunchFromNotification, true)
        val pendingIntent= PendingIntent
            .getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)

        // configure options
        val beautifulTimestamp = beautifyTimestamp(timestamp)
        val builder = NotificationCompat.Builder(this, getString(R.string.notification_channel_id))
            .setSmallIcon(R.drawable.logo_square)
            .setContentTitle(message)
            .setContentText(beautifulTimestamp)
            .setStyle(NotificationCompat.BigTextStyle()
                .bigText(beautifulTimestamp))
            .setContentIntent(pendingIntent)
            .setAutoCancel(true)

        // show notification
        NotificationManagerCompat.from(this).notify(
            trimTimestamp(timestamp),
            builder.build())
    }
}

fun signOut() =
    Firebase.auth.signOut()

