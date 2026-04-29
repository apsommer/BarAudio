package com.sommerengineering.signalvoice.firebase

import com.google.firebase.Firebase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.database
import com.sommerengineering.signalvoice.source.Message
import com.sommerengineering.signalvoice.uitls.databaseUrl
import com.sommerengineering.signalvoice.uitls.messageKey
import com.sommerengineering.signalvoice.uitls.sourceKey
import com.sommerengineering.signalvoice.uitls.streamsNode
import com.sommerengineering.signalvoice.uitls.tokensNode
import com.sommerengineering.signalvoice.uitls.usersNode
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume

@Singleton
class FirebaseDatabaseImpl @Inject constructor() {

    private val db = Firebase.database(databaseUrl)
    private var uid: String? = null

    fun setUid(newUid: String) {
        if (uid == newUid) return
        uid = newUid
    }

    suspend fun fetchStreamMessages(stream: String) =
        suspendCancellableCoroutine { continuation ->
            db.getReference(streamsNode)
                .child(stream)
                .get()
                .addOnSuccessListener { snapshot ->
                    val messages = snapshot.children.mapNotNull {
                        it.toStreamMessage(stream)
                    }
                    continuation.resume(messages)
                }.addOnFailureListener { continuation.resume(emptyList()) }
        }

    suspend fun fetchUserMessages(): List<Message> {

        // guest user has no messages
        val currentUid = uid ?: return emptyList()

        return suspendCancellableCoroutine { continuation ->
            db.getReference(usersNode)
                .child(currentUid)
                .get()
                .addOnSuccessListener { snapshot ->
                    val messages = snapshot.children.mapNotNull {
                        it.toUserMessage()
                    }
                    continuation.resume(messages)
                }.addOnFailureListener { continuation.resume(emptyList()) }
        }
    }

    private fun DataSnapshot.toStreamMessage(stream: String): Message? {

        // validate attributes
        val timestamp = key ?: return null
        val message = child(messageKey).value as? String ?: return null

        return Message(timestamp, message, stream, null)
    }

    private fun DataSnapshot.toUserMessage(): Message? {

        // validate attributes
        val timestamp = key ?: return null
        val message = child(messageKey).value as? String ?: return null
        val source = child(sourceKey).value as? String ?: return null

        return Message(timestamp, message, null, source)
    }

    fun writeToken(newToken: String) {
        db.getReference(tokensNode)
            .child(newToken)
            .setValue(uid ?: "")
    }
}