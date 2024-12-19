package com.sommerengineering.baraudio

import android.Manifest
import android.app.PendingIntent
import android.content.Intent
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import org.koin.android.ext.android.get

var token = unauthenticatedToken

class FirebaseService: FirebaseMessagingService() {

    private val tts: TextToSpeechImpl = get()

    override fun onNewToken(newToken: String) {

        token = newToken
        writeToDataStore(applicationContext, tokenKey, token)
        logMessage("New token: $token")
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {

        // extract attributes
        val uid = remoteMessage.data[uidKey] ?: return
        val timestamp = remoteMessage.data[timestampKey] ?: return
        val message = remoteMessage.data[messageKey] ?: return

        // either speak, or show notification
        var isShowNotification =
            Firebase.auth.currentUser == null || // user not signed-in
            !isAppOpen // app closed

        // note for different user, same device
        // since subscription is per google play user, db could be simplified to just one node of
        // tokens -> timestamp: message ... leave for now 191224
        val note =
            if (uid != Firebase.auth.currentUser?.uid) {
                isShowNotification = true
                unauthenticatedTimestampNote
            } else { "" }

        // either speak, or show notification
        if (isShowNotification) {
            showNotification(
                timestamp,
                message,
                note)

        } else {
            tts.speak(
                timestamp,
                message)
        }
    }

    private fun showNotification(
        timestamp: String,
        message: String,
        note: String) {

        // confirm permission granted
        if (ActivityCompat.checkSelfPermission(this, Manifest.permission.POST_NOTIFICATIONS)
            != PackageManager.PERMISSION_GRANTED) { return }

        // create pending intent to activity
        val intent = Intent(this, MainActivity::class.java)
            .putExtra(isLaunchFromNotification, true)
        val pendingIntent= PendingIntent
            .getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)

        // configure options
        val timestampWithNote =
            beautifyTimestamp(timestamp) + note

        val builder = NotificationCompat.Builder(this, getString(R.string.notification_channel_id))
            .setSmallIcon(R.drawable.logo_square)
            .setColor(ContextCompat.getColor(this, R.color.logo_blue))
            .setContentTitle(message)
            .setContentText(timestampWithNote) // collapsed
            .setStyle(NotificationCompat.BigTextStyle().bigText(timestampWithNote)) // expanded
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

var isDatabaseInitialized = false
fun getDatabaseReference(
    node: String)
: DatabaseReference {

    // singleton, enable local persistence can only be set once
    if (!isDatabaseInitialized) {

        // enable local cache
        Firebase
            .database(databaseUrl)
            .setPersistenceEnabled(true)

        isDatabaseInitialized = true
    }

    val uid = Firebase.auth.currentUser?.uid ?: unauthenticatedUser

    return Firebase
        .database(databaseUrl)
        .getReference(node)
        .child(uid)
}

fun writeTokenToDatabase() {

    val user = Firebase.auth.currentUser ?: return
    logMessage("Sign-in success")
    logMessage("    uid: ${user.uid}")
    logMessage("  token: $token")

    // write user:token pair to database, no write occurs if correct token already present
    getDatabaseReference(usersNode)
        .setValue(token)
}