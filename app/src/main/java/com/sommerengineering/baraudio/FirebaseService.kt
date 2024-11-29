package com.sommerengineering.baraudio

import android.Manifest
import android.app.PendingIntent
import android.content.Context
import android.content.Intent
import android.content.pm.PackageManager
import android.media.AudioManager
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import org.koin.android.ext.android.get

var token = ""

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
        val timestamp = remoteMessage.data[timestampKey] ?: return
        val message = remoteMessage.data[messageKey] ?: return

        // either speak, or show notification
        val isShowNotification =
            Firebase.auth.currentUser == null ||
            !isAppOpen ||
            tts.volume == 0f ||
            getSystemVolume() == 0

        if (isShowNotification && !isAppForeground) { showNotification(timestamp, message) }
        else { tts.speak(timestamp, message) }
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

    fun getSystemVolume() =
        (applicationContext
            .getSystemService(Context.AUDIO_SERVICE) as AudioManager)
            .getStreamVolume(AudioManager.STREAM_MUSIC) // between 0-25
}

val dbRef by lazy {

    // enable local cache
    Firebase
        .database(databaseUrl)
        .setPersistenceEnabled(true)

    val uid = Firebase.auth.currentUser?.uid ?: unauthUser

    Firebase
        .database(databaseUrl)
        .getReference(messages)
        .child(uid)
}

fun signOut() =
    Firebase.auth.signOut()

fun validateToken() {

    val user = Firebase.auth.currentUser ?: return
    logMessage("Sign-in success with user: ${user.uid}")

    user
        .getIdToken(false)
        .addOnSuccessListener {

            // compare correct cached token with user token (potentially invalid)
            if (it.token == token) {
                logMessage("Token already in cache, skipping database write")
                return@addOnSuccessListener
            }

            // update database user:token association in database, if needed
            Firebase.database(databaseUrl)
                .getReference(users)
                .child(user.uid)
                .setValue(token)

            logMessage("New user: token pair written to database")
        }
}