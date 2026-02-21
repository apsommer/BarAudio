package com.sommerengineering.baraudio.firebase

import android.Manifest
import android.app.PendingIntent
import android.content.Intent
import android.content.pm.PackageManager
import android.os.PowerManager
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
import com.sommerengineering.baraudio.ProcessState
import com.sommerengineering.baraudio.R
import com.sommerengineering.baraudio.messages.Message
import com.sommerengineering.baraudio.speak.ForegroundSpeechService
import com.sommerengineering.baraudio.uitls.broadcastKey
import com.sommerengineering.baraudio.uitls.messageKey
import com.sommerengineering.baraudio.uitls.originKey
import com.sommerengineering.baraudio.uitls.timestampKey
import com.sommerengineering.baraudio.uitls.uidKey
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class FirebaseServiceImpl: FirebaseMessagingService() {

    @Inject lateinit var repo: MainRepository
    @Inject lateinit var processState: ProcessState

    override fun onMessageReceived(remoteMessage: RemoteMessage) {

        // foreground: system delivers notification 'data' payload
            // system does not show notification
        // background: system delivers notification 'data' payload
            // system attempts to show notification so it must be manually suppressed
        // no app process: no payload is delivered (this method not called)
            // system shows notification defined with params in manifest

        // extract attributes
        val broadcast = remoteMessage.data[broadcastKey]
        val uid = remoteMessage.data[uidKey]
        val timestamp = remoteMessage.data[timestampKey] ?: return
        val message = remoteMessage.data[messageKey] ?: return
        val origin = remoteMessage.data[originKey] ?: return

        // catch malformed message
        if (broadcast == null && uid == null) return

        // catch different user on same device
        if (uid != null && uid != Firebase.auth.currentUser?.uid) return

        // create message
        val newMessage = Message(timestamp, message, origin)
        repo.addMessage(newMessage)

        // determine if screen on
        val powerManager = getSystemService(POWER_SERVICE) as PowerManager
        val isScreenOn = powerManager.isInteractive

        // speak if app foreground/background and screen on, else notification
        val shouldSpeak = processState.isAlive && isScreenOn && Firebase.auth.currentUser != null
        if (shouldSpeak) {
            ForegroundSpeechService.start(this, newMessage)
        }
    }

    override fun onNewToken(token: String) =
        repo.onNewToken(token)
}