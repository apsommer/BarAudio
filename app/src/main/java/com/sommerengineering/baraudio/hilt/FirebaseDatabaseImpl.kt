package com.sommerengineering.baraudio.hilt

import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.sommerengineering.baraudio.logException
import com.sommerengineering.baraudio.messageKey
import com.sommerengineering.baraudio.messageMaxSize
import com.sommerengineering.baraudio.messages.Message
import com.sommerengineering.baraudio.messages.originParsingError
import com.sommerengineering.baraudio.messagesNode
import com.sommerengineering.baraudio.originKey
import com.sommerengineering.baraudio.messageParsingError
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import org.json.JSONException
import org.json.JSONObject

class FirebaseDatabaseImpl {

    val _messages = MutableStateFlow<List<Message>>(emptyList())
    val messages = _messages.asStateFlow()

    private var listener: ChildEventListener? = null
    private val cache = mutableListOf<Message>()

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

                // add message to list
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

                _messages.update { cache.toList() }
            }

            // do nothing
            override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) { }
            override fun onChildRemoved(snapshot: DataSnapshot) { }
            override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) { }
            override fun onCancelled(error: DatabaseError) { }
        }

        // attach listener to database, triggers once for every child on initial connection
        getDatabaseReference(messagesNode)
            .limitToLast(100)
            .addChildEventListener(newListener)

        this@FirebaseDatabaseImpl.listener = newListener
    }

    fun deleteMessage(message: Message) {

        cache.remove(message)
        _messages.update { cache.toList() }

        // sync server
        getDatabaseReference(messagesNode)
            .child(message.timestamp)
            .removeValue()
    }

    fun deleteAllMessages() {

        cache.clear()
        _messages.update { cache.toList() }

        // remove the entire node to remove all children
        getDatabaseReference(messagesNode).removeValue()
    }

    fun stopListening() {

        // detach listener from database
        listener?.let { getDatabaseReference(messagesNode).removeEventListener(it) }
        listener = null
    }
}