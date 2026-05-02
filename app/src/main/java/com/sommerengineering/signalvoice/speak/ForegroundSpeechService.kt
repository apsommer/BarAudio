package com.sommerengineering.signalvoice.speak

import android.app.Notification
import android.app.PendingIntent
import android.app.Service
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.sommerengineering.signalvoice.MainRepository
import com.sommerengineering.signalvoice.R
import com.sommerengineering.signalvoice.uitls.channelId
import com.sommerengineering.signalvoice.uitls.notificationId
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.launch
import javax.inject.Inject

const val ACTION_DISMISS = "ACTION_DISMISS"

@AndroidEntryPoint
class ForegroundSpeechService : Service() {

    @Inject
    lateinit var repo: MainRepository
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)
    private var isObserving = false

    companion object {
        fun start(context: Context) =
            ContextCompat.startForegroundService(
                context,
                Intent(context, ForegroundSpeechService::class.java)
            )

        fun stop(context: Context) =
            context.stopService(
                Intent(context, ForegroundSpeechService::class.java)
            )
    }

    override fun onStartCommand(
        intent: Intent?,
        flags: Int,
        startId: Int
    ): Int {

        // stop service when dismissed
        if (intent?.action == ACTION_DISMISS) {
            repo.setListening(false)
            stopForeground(STOP_FOREGROUND_REMOVE)
            stopSelf()
            return START_NOT_STICKY
        }

        // dedupe, check if service already running
        if (isObserving) return START_STICKY
        isObserving = true

        // create initial notification
        val waitingNotification = buildNotification()

        // show notification
        startForeground(notificationId, waitingNotification)

        // update notification and speak message
        serviceScope.launch {

            var lastTimestamp: String? = null

            repo.messages.collect { messages ->

                // seed lastTimestamp with latest
                val message = messages.firstOrNull() ?: return@collect
                if (lastTimestamp == null) {
                    lastTimestamp = message.timestamp
                    return@collect
                }

                // dedupe
                if (message.timestamp == lastTimestamp) return@collect
                lastTimestamp = message.timestamp

                // speak message
                repo.speakMessage(message)
            }
        }
        return START_STICKY
    }

    private fun buildNotification(): Notification {

        // launch app when clicked
        val launchIntent = packageManager.getLaunchIntentForPackage(packageName)
        val pendingOpenAppIntent = launchIntent?.let {
            PendingIntent.getActivity(
                this,
                0,
                it,
                PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
            )
        }

        // stop service when dismissed
        val dismissIntent = Intent(
            this,
            ForegroundSpeechService::class.java
        ).apply {
            action = ACTION_DISMISS
        }
        val pendingDismissIntent = PendingIntent.getService(
            this,
            1,
            dismissIntent,
            PendingIntent.FLAG_IMMUTABLE or PendingIntent.FLAG_UPDATE_CURRENT
        )

        return NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.monochrome)
            .setContentTitle("Listening for signals")
            .setContentIntent(pendingOpenAppIntent)
            .setDeleteIntent(pendingDismissIntent)
            .setForegroundServiceBehavior(NotificationCompat.FOREGROUND_SERVICE_IMMEDIATE)
            .setVisibility(NotificationCompat.VISIBILITY_PRIVATE)
            .setOnlyAlertOnce(true)
            .setOngoing(true)
            .build()
    }

    override fun onTaskRemoved(rootIntent: Intent?) {
        super.onTaskRemoved(rootIntent)

        // stop service when user closes app (not system)
        stopForeground(STOP_FOREGROUND_REMOVE) // removes notification
        stopSelf() // kills service
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
        isObserving = false
    }

    override fun onBind(p0: Intent?) = null
}