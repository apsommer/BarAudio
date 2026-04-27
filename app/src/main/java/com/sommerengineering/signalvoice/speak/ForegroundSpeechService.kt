package com.sommerengineering.signalvoice.speak

import android.app.Service
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.sommerengineering.signalvoice.MainRepository
import com.sommerengineering.signalvoice.R
import com.sommerengineering.signalvoice.source.Message
import com.sommerengineering.signalvoice.uitls.TimestampFormatter
import com.sommerengineering.signalvoice.uitls.channelId
import com.sommerengineering.signalvoice.uitls.notificationId
import com.sommerengineering.signalvoice.uitls.notificationKey
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ForegroundSpeechService : Service() {

    @Inject
    lateinit var repo: MainRepository
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    companion object {

        fun start(
            context: Context,
            message: Message
        ) {

            val intent = Intent(context, ForegroundSpeechService::class.java)
            intent.putExtra(notificationKey, message)
            ContextCompat.startForegroundService(context, intent)
        }
    }

    override fun onStartCommand(
        intent: Intent?,
        flags: Int,
        startId: Int
    ): Int {

        // validate payload
        if (intent == null) return START_NOT_STICKY
        val message = intent.getParcelableExtra<Message>(notificationKey)
            ?: run {
                stopSelf()
                return START_NOT_STICKY // system will not recreate service
            }

        // extract attributes
        val beautifulTimestamp = TimestampFormatter.beautifyTime(message.timestamp)
        val title = message.message

        // create notification
        val notification = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.monochrome) // status bar
            .setColor(ContextCompat.getColor(this, R.color.app_blue))
            .setContentTitle(title)
            .setContentText(beautifulTimestamp) // collapsed
            .setStyle(NotificationCompat.BigTextStyle().bigText(beautifulTimestamp)) // expanded
            .setAutoCancel(true)
            .build()

        // show notification
        startForeground(notificationId, notification)

        // speak message, then stop service (remove notification)
        serviceScope.launch {
            repo.speakMessage(message)
            stopForeground(STOP_FOREGROUND_REMOVE)
            stopSelf()
        }

        return START_NOT_STICKY
    }


    override fun onBind(p0: Intent?) = null
}