package com.sommerengineering.baraudio.hilt

import com.google.firebase.Firebase
import com.google.firebase.auth.auth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.database
import com.sommerengineering.baraudio.databaseUrl
import com.sommerengineering.baraudio.logException
import com.sommerengineering.baraudio.messageKey
import com.sommerengineering.baraudio.messageMaxSize
import com.sommerengineering.baraudio.messageParsingError
import com.sommerengineering.baraudio.messages.Message
import com.sommerengineering.baraudio.messages.parsingErrorOrigin
import com.sommerengineering.baraudio.messagesNodeId
import com.sommerengineering.baraudio.originKey
import com.sommerengineering.baraudio.usersNodeId
import com.sommerengineering.baraudio.whitelistNodeId
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import org.json.JSONException
import org.json.JSONObject

class FirebaseDatabaseImpl {

    private val db = Firebase.database(databaseUrl)
    private val messagesNode = db.getReference(messagesNodeId)
    private var listener: ChildEventListener? = null
    private lateinit var uid: String

    val _messages = MutableStateFlow<List<Message>>(emptyList())
    val messages = _messages.asStateFlow()
    private val cache = mutableListOf<Message>()

    fun setUid(newUid: String) {

        // check if already set
        if (::uid.isInitialized && uid == newUid) return

        stopListening()
        cache.clear()
        _messages.update { cache.toList() }

        uid = newUid
    }

    fun startListening() {

        // ensure only one listener exists
        if (listener != null) return

        val newListener = object: ChildEventListener {

            override fun onChildAdded(
                snapshot: DataSnapshot,
                previousChildName: String?) {

                // extract attributes
                val timestamp = snapshot.key
                val rawMessage = snapshot.value.toString()

                // dedupe safety for rare firebase replay
                if (cache.any { it.timestamp == timestamp }) return

                // reject malformed message
                if (timestamp.isNullOrEmpty() || rawMessage.isEmpty()) return

                // parse json
                var message: String
                var origin: String

                try {
                    val json = JSONObject(rawMessage)
                    message = json.getString(messageKey)
                    origin = json.getString(originKey)

                } catch (e: JSONException) {
                    logException(e)
                    message = messageParsingError
                    origin = parsingErrorOrigin
                }

                // add message to cache
                cache.add(
                    Message(
                        timestamp = timestamp,
                        message = message,
                        origin = origin))

                // order by timestamp, descending
                cache.sortBy { it.timestamp }

                // limit size
                if (cache.size > messageMaxSize) {
                    deleteMessage(cache[messageMaxSize])
                }

                // emit cache
                _messages.update { cache.toList() }
            }

            // do nothing
            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) { }
            override fun onChildRemoved(snapshot: DataSnapshot) { }
            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) { }
            override fun onCancelled(error: DatabaseError) { }
        }

        // attach listener to messages/user node
        val messagesUserNode = messagesNode.child(uid)
        messagesUserNode.limitToLast(messageMaxSize).addChildEventListener(newListener)
        messagesUserNode.keepSynced(true)

        listener = newListener
    }

    fun stopListening() {
        listener?.let { messagesNode.removeEventListener(it) }
        listener = null
    }

    fun deleteMessage(message: Message) {
        cache.remove(message)
        _messages.update { cache.toList() }
        messagesNode
            .child(uid)
            .child(message.timestamp)
            .removeValue()
    }

    fun deleteAllMessages() {
        cache.clear()
        _messages.update { cache.toList() }
        messagesNode.child(uid).removeValue()
    }

    fun writeToken(token: String) {
        db.getReference(usersNodeId).child(uid).setValue(token)
    }

    fun writeWhitelist(enabled: Boolean) {
        db.getReference(whitelistNodeId).child(uid).setValue(enabled)
    }
}
