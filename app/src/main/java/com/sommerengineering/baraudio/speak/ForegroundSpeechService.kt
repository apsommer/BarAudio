//package com.sommerengineering.baraudio.speak
//
//import android.app.Service
//import android.content.Context
//import android.content.Intent
//import android.os.IBinder
//import com.sommerengineering.baraudio.MainRepository
//import com.sommerengineering.baraudio.messages.Message
//import com.sommerengineering.baraudio.uitls.channelId
//import com.sommerengineering.baraudio.uitls.messageKey
//import dagger.hilt.android.AndroidEntryPoint
//import javax.inject.Inject
//
//@AndroidEntryPoint
//class ForegroundSpeechService : Service() {
//
//    @Inject lateinit var repo: MainRepository
//
//    companion object {
//
//        fun start(context: Context, message: Message) {
//            val intent = Intent(context, ForegroundSpeechService::class.java)
//            intent.putExtra(messageKey, message)
//            context.startService(intent)
//        }
//    }
//
//    override fun onStartCommand(intent: Intent?, flags: Int, startId: Int): Int {
//
//        val message = intent?.getParcelableExtra<Message>(messageKey) ?: return START_NOT_STICKY
//
//        startForeground(channelId, buildNotification())
//
//        repo.speakMessage(message) {
//            stopForeground(true)
//            stopSelf()
//        }
//    }
//
//
//
//
//
//
//
//
//
//
//
//
//    override fun onBind(p0: Intent?) = null
//}