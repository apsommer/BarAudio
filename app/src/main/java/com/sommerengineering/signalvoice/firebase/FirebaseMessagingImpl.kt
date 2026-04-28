package com.sommerengineering.signalvoice.firebase

import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.messaging.FirebaseMessagingService
import com.google.firebase.messaging.RemoteMessage
import com.sommerengineering.signalvoice.MainRepository
import com.sommerengineering.signalvoice.ProcessState
import com.sommerengineering.signalvoice.source.Message
import com.sommerengineering.signalvoice.speak.ForegroundSpeechService
import com.sommerengineering.signalvoice.uitls.streamKey
import com.sommerengineering.signalvoice.uitls.messageKey
import com.sommerengineering.signalvoice.uitls.sourceKey
import com.sommerengineering.signalvoice.uitls.timestampKey
import com.sommerengineering.signalvoice.uitls.uidKey
import dagger.hilt.android.AndroidEntryPoint
import javax.inject.Inject

@AndroidEntryPoint
class FirebaseServiceImpl : FirebaseMessagingService() {

    @Inject
    lateinit var repo: MainRepository
    @Inject
    lateinit var processState: ProcessState

    override fun onMessageReceived(remoteMessage: RemoteMessage) {

        // ensure app is foreground or background
        if (!processState.isTaskAlive) return

        // convert remote message to domain model
        val message = remoteMessage.toMessage() ?: return

        repo.addMessage(message)
        ForegroundSpeechService.start(this, message)
    }

    private fun RemoteMessage.toMessage(): Message? {

        // message is broadcast from stream, or send to specific user device
        val stream = data[streamKey]
        val uid = data[uidKey]
        if (stream == null && uid == null) return null

        // catch different user on same device
        val currentUid = Firebase.auth.currentUser?.uid ?: return null
        if (uid != null && uid != currentUid) return null

        // validate payload
        val timestamp = data[timestampKey] ?: return null
        val message = data[messageKey] ?: return null
        val source = data[sourceKey]

        return Message(timestamp, message, stream, source)
    }

    override fun onNewToken(token: String) =
        repo.onNewToken(token)
}