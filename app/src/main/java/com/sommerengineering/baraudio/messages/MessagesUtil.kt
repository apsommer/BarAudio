package com.sommerengineering.baraudio.messages

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.sommerengineering.baraudio.MainViewModel
import com.sommerengineering.baraudio.getDatabaseReference
import com.sommerengineering.baraudio.logException
import com.sommerengineering.baraudio.messageKey
import com.sommerengineering.baraudio.messageMaxSize
import com.sommerengineering.baraudio.messagesNode
import com.sommerengineering.baraudio.originKey
import com.sommerengineering.baraudio.parsingError
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.json.JSONException
import org.json.JSONObject

lateinit var dbListener: ChildEventListener

fun listenToDatabase(
    messages: SnapshotStateList<Message>,
    viewModel: MainViewModel,
    listState: LazyListState,
    coroutine: CoroutineScope) {

    dbListener = object: ChildEventListener {

        override fun onChildAdded(
            snapshot: DataSnapshot,
            previousChildName: String?) {

            // extract attributes
            val timestamp = snapshot.key
            val rawMessage = snapshot.value.toString()

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
                message = parsingError
                origin = error
            }

            // todo observe a State<LinkedList> to reverse order efficiently
            messages.add(0, Message(timestamp, message, origin))
            coroutine.launch { listState.scrollToItem(0) }

            // limit size
            if (messages.size > messageMaxSize) {

                deleteMessage(
                    messages = messages,
                    message = messages[messageMaxSize])
            }
        }

        // do nothing
        override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) { }
        override fun onChildRemoved(snapshot: DataSnapshot) { }
        override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) { }
        override fun onCancelled(error: DatabaseError) { }
    }

    // triggers once for every child on initial connection
    getDatabaseReference(messagesNode)
        .addChildEventListener(dbListener)
}

fun deleteMessage(
    messages: SnapshotStateList<Message>,
    message: Message) {

    getDatabaseReference(messagesNode)
        .child(message.timestamp)
        .removeValue()

    messages
        .remove(message)
}

fun deleteAllMessages(
    messages: SnapshotStateList<Message>) {

    getDatabaseReference(messagesNode)
        .removeValue()

    messages
        .clear()
}