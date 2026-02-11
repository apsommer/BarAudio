package com.sommerengineering.baraudio.hilt

import com.google.firebase.Firebase
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.database
import com.sommerengineering.baraudio.databaseUrl
import com.sommerengineering.baraudio.logException
import com.sommerengineering.baraudio.messageKey
import com.sommerengineering.baraudio.messageMaxSize
import com.sommerengineering.baraudio.messages.Message
import com.sommerengineering.baraudio.messages.originParsingError
import com.sommerengineering.baraudio.messagesNodeId
import com.sommerengineering.baraudio.originKey
import com.sommerengineering.baraudio.messageParsingError
import com.sommerengineering.baraudio.uid
import com.sommerengineering.baraudio.usersNodeId
import com.sommerengineering.baraudio.whitelistNodeId
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import org.json.JSONException
import org.json.JSONObject

class FirebaseDatabaseImpl {

    private val db: FirebaseDatabase
    private val messagesNode: DatabaseReference
    private var listener: ChildEventListener? = null

    val _messages = MutableStateFlow<List<Message>>(emptyList())
    val messages = _messages.asStateFlow()
    private val cache = mutableListOf<Message>()

    init {

        // enable offline mode with local persistence
        Firebase
            .database(databaseUrl)
            .setPersistenceEnabled(true)

        db = Firebase.database(databaseUrl)
        messagesNode = db.getReference(messagesNodeId).child(uid)
        messagesNode.keepSynced(true)
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
                    origin = originParsingError
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

        // attach listener to database
        messagesNode
            .limitToLast(100)
            .addChildEventListener(newListener)

        listener = newListener
    }

    fun deleteMessage(message: Message) {

        cache.remove(message)
        _messages.update { cache.toList() }
        messagesNode
            .child(message.timestamp)
            .removeValue()
    }

    fun deleteAllMessages() {

        cache.clear()
        _messages.update { cache.toList() }
        messagesNode.removeValue()
    }

    fun stopListening() {

        listener?.let { messagesNode.removeEventListener(it) }
        listener = null
    }

    fun writeToken(token: String) {
        db.getReference(usersNodeId).setValue(token)
    }

    fun writeWhitelist(enabled: Boolean) {
        db.getReference(whitelistNodeId).setValue(enabled)
    }
}
