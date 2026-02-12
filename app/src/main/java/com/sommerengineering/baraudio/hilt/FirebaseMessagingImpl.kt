package com.sommerengineering.baraudio.hilt

import android.Manifest
import android.app.PendingIntent
import android.content.Intent
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.core.content.ContextCompat
import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.sommerengineering.baraudio.MainActivity
import com.sommerengineering.baraudio.MainRepository
import com.sommerengineering.baraudio.R
import com.sommerengineering.baraudio.channelId
import com.sommerengineering.baraudio.isLaunchFromNotification
import com.sommerengineering.baraudio.messageKey
import com.sommerengineering.baraudio.messages.Message
import com.sommerengineering.baraudio.messages.beautifyTimestamp
import com.sommerengineering.baraudio.originKey
import com.sommerengineering.baraudio.timestampKey
import com.sommerengineering.baraudio.uidKey
import com.sommerengineering.baraudio.unauthenticatedTimestampNote
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class FirebaseServiceImpl: FirebaseMessagingService() {

    @Inject lateinit var repo: MainRepository
    @Inject lateinit var appVisibility: AppVisibility

    override fun onMessageReceived(remoteMessage: RemoteMessage) {

        // extract attributes
        val uid = remoteMessage.data[uidKey] ?: return
        val timestamp = remoteMessage.data[timestampKey] ?: return
        val message = remoteMessage.data[messageKey] ?: return
        val origin = remoteMessage.data[originKey] ?: return

        // either speak, or show notification
        var isShowNotification =
            Firebase.auth.currentUser == null || // user not signed-in
            !appVisibility.isForeground // app closed

        // note for different user, same device
        val note =
            if (uid != Firebase.auth.currentUser?.uid) {
                isShowNotification = true
                unauthenticatedTimestampNote
            } else { "" }

        // show notification
        if (isShowNotification) {
            showNotification(timestamp, message, note)
            return
        }

        // speak message
        repo.speakMessage(
            Message(
                timestamp = timestamp,
                message = message,
                origin = origin))

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

        val builder = NotificationCompat.Builder(this, channelId)
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

    private fun trimTimestamp(timestamp: String) =
        timestamp
            .substring(timestamp.length - 9, timestamp.length)
            .toInt()

    override fun onNewToken(token: String) = repo.onNewToken(token)
}
