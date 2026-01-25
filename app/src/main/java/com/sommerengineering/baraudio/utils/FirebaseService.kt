package com.sommerengineering.baraudio.utils

import android.Manifest
import android.app.PendingIntent
import android.content.Intent
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.google.firebase.auth.auth
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.database
import com.google.firebase.Firebase
import com.google.firebase.messaging.RemoteMessage
import org.koin.android.ext.android.get

var token = _root_ide_package_.com.sommerengineering.baraudio.unauthenticatedToken

class FirebaseService: com.google.firebase.messaging.FirebaseMessagingService() {

    private val tts: TextToSpeechImpl = get()

    override fun onNewToken(newToken: String) {

        token = newToken
        writeToDataStore(
            applicationContext,
            _root_ide_package_.com.sommerengineering.baraudio.tokenKey,
            token
        )
        logMessage("New token: $token")
    }

    override fun onMessageReceived(remoteMessage: RemoteMessage) {

        // extract attributes
        val uid = remoteMessage.data[_root_ide_package_.com.sommerengineering.baraudio.uidKey] ?: return
        val timestamp = remoteMessage.data[_root_ide_package_.com.sommerengineering.baraudio.timestampKey] ?: return
        val message = remoteMessage.data[_root_ide_package_.com.sommerengineering.baraudio.messageKey] ?: return

        // either speak, or show notification
        var isShowNotification =
            Firebase.auth.currentUser == null || // user not signed-in
            !_root_ide_package_.com.sommerengineering.baraudio.isAppOpen // app closed todo ugly, refactor?

        // note for different user, same device
        val note =
            if (uid != Firebase.auth.currentUser?.uid) {
                isShowNotification = true
                _root_ide_package_.com.sommerengineering.baraudio.unauthenticatedTimestampNote
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
        val intent = Intent(this, _root_ide_package_.com.sommerengineering.baraudio.MainActivity::class.java)
            .putExtra(_root_ide_package_.com.sommerengineering.baraudio.isLaunchFromNotification, true)
        val pendingIntent= PendingIntent
            .getActivity(this, 0, intent, PendingIntent.FLAG_IMMUTABLE)

        // configure options
        val timestampWithNote =
            beautifyTimestamp(timestamp) + note

        val builder = NotificationCompat.Builder(this,
            _root_ide_package_.com.sommerengineering.baraudio.channelId
        )
            .setSmallIcon(_root_ide_package_.com.sommerengineering.baraudio.R.drawable.logo_square)
            .setColor(ContextCompat.getColor(this, _root_ide_package_.com.sommerengineering.baraudio.R.color.logo_blue))
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

    private fun trimTimestamp(timestamp: String) =
        timestamp
            .substring(timestamp.length - 9, timestamp.length)
            .toInt()
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
            .database(_root_ide_package_.com.sommerengineering.baraudio.databaseUrl)
            .setPersistenceEnabled(true)

        isDatabaseInitialized = true
    }

    val uid = Firebase.auth.currentUser?.uid ?: _root_ide_package_.com.sommerengineering.baraudio.unauthenticatedUser

    return Firebase
        .database(_root_ide_package_.com.sommerengineering.baraudio.databaseUrl)
        .getReference(node)
        .child(uid)
}

fun writeTokenToDatabase() {

    val user = Firebase.auth.currentUser ?: return

    // write user:token pair to database, no write occurs if correct token already present
    getDatabaseReference(_root_ide_package_.com.sommerengineering.baraudio.usersNode)
        .setValue(token)

    logMessage("Sign-in success")
    logMessage("    uid: ${user.uid}")
    logMessage("  token: $token")
}

fun writeWhitelistToDatabase(
    isWhitelist: Boolean) {

    // write user to whitelist database, no write occurs if correct value already present
    getDatabaseReference(_root_ide_package_.com.sommerengineering.baraudio.whitelistNode)
        .setValue(isWhitelist)

    logMessage("isWhitelist: ${isWhitelist}")
}
