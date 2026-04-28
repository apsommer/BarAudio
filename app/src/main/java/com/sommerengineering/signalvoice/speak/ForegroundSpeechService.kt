package com.sommerengineering.signalvoice.speak

import android.app.NotificationManager
import android.app.Service
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.sommerengineering.signalvoice.MainRepository
import com.sommerengineering.signalvoice.R
import com.sommerengineering.signalvoice.uitls.TimestampFormatter
import com.sommerengineering.signalvoice.uitls.channelId
import com.sommerengineering.signalvoice.uitls.notificationId
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.cancel
import kotlinx.coroutines.flow.firstOrNull
import kotlinx.coroutines.launch
import javax.inject.Inject

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

        // dedupe, check if service already running
        if (isObserving) return START_STICKY
        isObserving = true

        // create initial notification
        val notification = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.monochrome) // status bar
            .setColor(ContextCompat.getColor(this, R.color.app_blue))
            .setContentTitle("Listening for signals")
            .setContentText("Waiting for activity") // collapsed
            .build()

        // show notification
        startForeground(notificationId, notification)

        // update notification and speak message
        serviceScope.launch {

            val manager = getSystemService(NOTIFICATION_SERVICE) as NotificationManager

            // seed lastTimestamp with latest
            val initialMessages = repo.messages.firstOrNull()
            var lastTimestamp = initialMessages?.firstOrNull()?.timestamp

            repo.messages.collect { messages ->

                // dedupe
                val message = messages.firstOrNull() ?: return@collect
                if (message.timestamp == lastTimestamp) return@collect
                lastTimestamp = message.timestamp

                // extract attributes
                val beautifulTimestamp = TimestampFormatter.beautifyTime(message.timestamp)
                val title = message.message

                // create updated notification
                val notification =
                    NotificationCompat.Builder(this@ForegroundSpeechService, channelId)
                        .setSmallIcon(R.drawable.monochrome) // status bar
                        .setColor(
                            ContextCompat.getColor(
                                this@ForegroundSpeechService,
                                R.color.app_blue
                            )
                        )
                        .setContentTitle(title)
                        .setContentText(beautifulTimestamp) // collapsed
                        .setStyle( // expanded
                            NotificationCompat.BigTextStyle().bigText(beautifulTimestamp)
                        )
                        .build()

                // update notification
                manager.notify(notificationId, notification)

                // speak message
                repo.speakMessage(message)
            }
        }
        return START_STICKY
    }

    override fun onDestroy() {
        super.onDestroy()
        serviceScope.cancel()
        isObserving = false
    }

    override fun onBind(p0: Intent?) = null
}