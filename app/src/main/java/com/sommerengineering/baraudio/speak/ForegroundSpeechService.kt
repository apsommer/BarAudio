package com.sommerengineering.baraudio.speak

import android.app.Service
import android.content.Context
import android.content.Intent
import androidx.core.app.NotificationCompat
import androidx.core.content.ContextCompat
import com.sommerengineering.baraudio.MainRepository
import com.sommerengineering.baraudio.R
import com.sommerengineering.baraudio.messages.Message
import com.sommerengineering.baraudio.uitls.TimestampFormatter
import com.sommerengineering.baraudio.uitls.channelId
import com.sommerengineering.baraudio.uitls.messageKey
import com.sommerengineering.baraudio.uitls.notificationId
import com.sommerengineering.baraudio.uitls.notificationKey
import dagger.hilt.android.AndroidEntryPoint
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch
import javax.inject.Inject

@AndroidEntryPoint
class ForegroundSpeechService : Service() {

    @Inject lateinit var repo: MainRepository
    private val serviceScope = CoroutineScope(SupervisorJob() + Dispatchers.Main)

    companion object {

        fun start(context: Context, message: Message) {
            val intent = Intent(context, ForegroundSpeechService::class.java)
            intent.putExtra(notificationKey, message)
            context.startService(intent)
        }
    }

    override fun onStartCommand(
        intent: Intent?,
        flags: Int,
        startId: Int
    ): Int {

        val message = intent?.getParcelableExtra<Message>(messageKey)
            ?: return START_NOT_STICKY

        val beautifulTimestamp = TimestampFormatter.beautify(message.timestamp)
        val title = message.message

        val builder = NotificationCompat.Builder(this, channelId)
            .setSmallIcon(R.drawable.logo_square)
            .setColor(ContextCompat.getColor(this, R.color.logo_blue))
            .setContentTitle(title)
            .setContentText(beautifulTimestamp) // collapsed
            .setStyle(NotificationCompat.BigTextStyle().bigText(beautifulTimestamp)) // expanded
            .setAutoCancel(true)

        startForeground(notificationId, builder.build())

        serviceScope.launch {
            repo.speakMessage(message)
            stopForeground(STOP_FOREGROUND_REMOVE)
            stopSelf()
        }

        return START_NOT_STICKY
    }













    override fun onBind(p0: Intent?) = null
}