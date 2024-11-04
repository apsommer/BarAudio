package com.sommerengineering.baraudio

import android.Manifest
import android.app.PendingIntent
import android.content.pm.PackageManager
import androidx.core.app.ActivityCompat
import androidx.core.app.NotificationCompat
import androidx.core.app.NotificationManagerCompat
import androidx.datastore.preferences.core.edit
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import org.koin.android.ext.android.get
import org.koin.android.ext.android.inject

class FirebaseService: FirebaseMessagingService() {

    private val tts: TextToSpeechImpl = get()
    private val viewModel: MainViewModel by inject()

    override fun onNewToken(token: String) =
        viewModel.writeToDataStore(applicationContext, tokenKey, token)

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