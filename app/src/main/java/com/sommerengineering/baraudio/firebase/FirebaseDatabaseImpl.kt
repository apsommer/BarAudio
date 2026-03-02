package com.sommerengineering.baraudio.firebase

import com.google.firebase.Firebase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.database
import com.sommerengineering.baraudio.source.Message
import com.sommerengineering.baraudio.uitls.databaseUrl
import com.sommerengineering.baraudio.uitls.messageKey
import com.sommerengineering.baraudio.uitls.sourceKey
import com.sommerengineering.baraudio.uitls.streamsNode
import com.sommerengineering.baraudio.uitls.tokensNode
import com.sommerengineering.baraudio.uitls.usersNode
import kotlinx.coroutines.suspendCancellableCoroutine
import javax.inject.Inject
import javax.inject.Singleton
import kotlin.coroutines.resume

@Singleton
class FirebaseDatabaseImpl @Inject constructor() {

    private val db = Firebase.database(databaseUrl)
    private lateinit var uid: String

    fun setUid(newUid: String) {
        if (::uid.isInitialized && uid == newUid) return
        uid = newUid
    }

    // todo refactor this to one fetch method?

    suspend fun fetchStreamMessages(stream: String): List<Message> =
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

    suspend fun fetchUserMessages(): List<Message> =
        suspendCancellableCoroutine { continuation ->
            db.getReference(usersNode)
                .child(uid)
                .get()
                .addOnSuccessListener { snapshot ->
                    val messages = snapshot.children.mapNotNull {
                        it.toUserMessage()
                    }
                    continuation.resume(messages)
                }.addOnFailureListener { continuation.resume(emptyList()) }
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

    fun writeToken(token: String) =
        db.getReference(tokensNode)
            .child(uid)
            .setValue(token)
}