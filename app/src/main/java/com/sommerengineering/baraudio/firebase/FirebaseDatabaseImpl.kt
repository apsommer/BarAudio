package com.sommerengineering.baraudio.firebase

import com.google.firebase.Firebase
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.database
import com.sommerengineering.baraudio.messages.Message
import com.sommerengineering.baraudio.uitls.databaseUrl
import com.sommerengineering.baraudio.uitls.messageKey
import com.sommerengineering.baraudio.uitls.originKey
import com.sommerengineering.baraudio.uitls.streamsNode
import com.sommerengineering.baraudio.uitls.tokensNode
import com.sommerengineering.baraudio.uitls.usersNode
import kotlinx.coroutines.suspendCancellableCoroutine
import kotlin.coroutines.resume

class FirebaseDatabaseImpl {

    private val db = Firebase.database(databaseUrl)
    private lateinit var uid: String

    fun setUid(newUid: String) {
        if (::uid.isInitialized && uid == newUid) return
        uid = newUid
    }

    suspend fun fetchStreamMessages(stream: String): List<Message> =
        suspendCancellableCoroutine { continuation ->
            db.getReference(streamsNode).child(stream).get()
                .addOnSuccessListener { snapshot ->
                    val messages = snapshot.children.mapNotNull { it.toMessage(stream) }
                    continuation.resume(messages)
                }.addOnFailureListener { continuation.resume(emptyList()) }
        }

    suspend fun fetchUserMessages(): List<Message> =
        suspendCancellableCoroutine { continuation ->
            db.getReference(usersNode).child(uid).get()
                .addOnSuccessListener { snapshot ->
                    val messages = snapshot.children.mapNotNull { it.toMessage() }
                    continuation.resume(messages)
                }.addOnFailureListener { continuation.resume(emptyList()) }
        }

    private fun DataSnapshot.toMessage(stream: String? = null): Message? {

        // validate attributes
        val timestamp = key ?: return null
        val message = child(messageKey).value as? String ?: return null
        val origin = stream ?: child(originKey).value as? String ?: return null

        return Message(timestamp, message, origin)
    }

    fun writeToken(token: String) =
        db.getReference(tokensNode)
            .child(uid)
            .setValue(token)
}