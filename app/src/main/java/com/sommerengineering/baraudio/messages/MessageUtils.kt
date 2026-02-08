package com.sommerengineering.baraudio.messages

import android.text.format.DateUtils
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.sommerengineering.baraudio.hilt.getDatabaseReference
import com.sommerengineering.baraudio.logException
import com.sommerengineering.baraudio.logMessage
import com.sommerengineering.baraudio.messageKey
import com.sommerengineering.baraudio.messageMaxSize
import com.sommerengineering.baraudio.messagesNode
import com.sommerengineering.baraudio.originKey
import com.sommerengineering.baraudio.parsingError
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.json.JSONException
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

fun listenToDatabase(
    messages: SnapshotStateList<Message>,
    listState: LazyListState,
    coroutine: CoroutineScope): ChildEventListener {

    val listener = object: ChildEventListener {

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
                logMessage(origin)

            } catch (e: JSONException) {
                logException(e)
                message = parsingError
                origin = error
            }

            // add message to list
            messages.add(
                Message(
                    timestamp = timestamp,
                    message = message,
                    origin = origin))

            coroutine.launch {
                listState.scrollToItem(0)
            }

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

    // attach listener to database
    // triggers once for every child on initial connection
    getDatabaseReference(messagesNode)
        .limitToLast(100)
        .addChildEventListener(listener)

    return listener
}

fun deleteMessage(
    messages: SnapshotStateList<Message>,
    message: Message) {

    getDatabaseReference(messagesNode)
        .child(message.timestamp)
        .removeValue()

    messages.remove(message)
}

fun deleteAllMessages(
    messages: SnapshotStateList<Message>) {

    // remove the entire node to remove all children
    getDatabaseReference(messagesNode).removeValue()

    messages.clear()
}

fun beautifyTimestamp(
    timestamp: String
): String {

    val isToday = DateUtils.isToday(timestamp.toLong())

    val pattern =
        if (isToday) "h:mm:ss a" // 6:27:53 PM
        else "h:mm:ss a • MMMM dd, yyyy" //  6:27:53 PM • October 30, 2024

    return SimpleDateFormat(
        pattern,
        Locale.getDefault())
        .format(Date(timestamp.toLong()))
}