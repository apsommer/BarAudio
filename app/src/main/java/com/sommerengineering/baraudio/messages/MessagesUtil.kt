package com.sommerengineering.baraudio.messages

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.sommerengineering.baraudio.MainViewModel
import com.sommerengineering.baraudio.dbRef
import com.sommerengineering.baraudio.logException
import com.sommerengineering.baraudio.messageKey
import com.sommerengineering.baraudio.messageMaxSize
import com.sommerengineering.baraudio.originKey
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.json.JSONException
import org.json.JSONObject

fun listenToDatabaseWrites(
    messages: SnapshotStateList<Message>,
    viewModel: MainViewModel,
    listState: LazyListState,
    coroutine: CoroutineScope) {

    // triggers once for every child on initial connection
    dbRef.addChildEventListener(object : ChildEventListener {

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
                message = "Error parsing message"
                origin = error
            }

            val imageId = viewModel.getOriginImageId(origin)

            // todo observe a State<LinkedList> to reverse order efficiently
            messages.add(0, Message(timestamp, message, imageId))
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
    })
}

fun swipeToDelete(
    messages: SnapshotStateList<Message>,
    message: Message,
    position: SwipeToDismissBoxValue
): Boolean {

    val startToEnd = SwipeToDismissBoxValue.StartToEnd
    val endToStart = SwipeToDismissBoxValue.EndToStart

    if (position != startToEnd && position != endToStart) return false

    deleteMessage(
        messages = messages,
        message = message)

    return true
}

fun deleteMessage(
    messages: SnapshotStateList<Message>,
    message: Message) {

    dbRef.child(message.timestamp).removeValue()
    messages.remove(message)
}

fun deleteAllMessages(
    messages: SnapshotStateList<Message>) {

    dbRef.removeValue()
    messages.clear()
}