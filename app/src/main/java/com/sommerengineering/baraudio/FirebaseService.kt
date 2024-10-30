package com.sommerengineering.baraudio

import android.Manifest
import android.app.PendingIntent
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.datastore.preferences.core.edit
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.koin.android.ext.android.get
import org.koin.java.KoinJavaComponent.inject

class FirebaseService : FirebaseMessagingService() {

    private val tts: TextToSpeechImpl = get()

    override fun onNewToken(token: String) {

        // write token to local cache
        CoroutineScope(Dispatchers.IO).launch {

            applicationContext.dataStore.edit {
                it[tokenKey] = token
            }

            logMessage("New token written to local cache: $token")
        }
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {
        handleMessage(remoteMessage)
    }



    private fun handleMessage(remoteMessage: RemoteMessage) {

        // extract attributes
        val timestamp = remoteMessage.data["timestamp"] ?: return
        val message = remoteMessage.data["message"] ?: return

        logMessage("FCM message received, $timestamp: $message")

        // show notification
        showNotification(timestamp, message)

        // only speak if app is open in background or foreground, not when closed
        if (!isAppOpen) { return }

        // speak message
        tts.message = message
        tts.speakMessage()
    }

    private fun showNotification(
        timestamp: String,
        message: String
    ) {

        // todo check that notifications have appropriate settings:
        //  importance, sound, etc at minimum levels, else show ui saying it's required
        //  also put link to system settings somewhere appropriate
        //  https://developer.android.com/develop/ui/views/notifications/channels#UpdateChannel

        // confirm permission granted
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
            != PackageManager.PERMISSION_GRANTED) { return }

        // create pending intent to activity
        val intent = packageManager.getLaunchIntentForPackage(getString(R.string.package_name))
        val pendingIntent= PendingIntent.getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)

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
            timestamp.toLong().toInt(),
            builder.build())
    }
}

fun writeNewUserToDatabase(token: String) {

    // get user id
    val firebaseAuth: FirebaseAuth by inject(FirebaseAuth::class.java)
    val uid = firebaseAuth.currentUser?.uid ?: return

    // write new user/token to database
    Firebase.database(databaseUrl)
        .getReference(users)
        .child(uid)
        .setValue(token)

    logMessage("New user: token written to database")
}