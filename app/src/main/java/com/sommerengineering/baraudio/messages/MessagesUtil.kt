package com.sommerengineering.baraudio.messages

import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.material3.SwipeToDismissBoxValue
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.sommerengineering.baraudio.dbRef
import com.sommerengineering.baraudio.message
import com.sommerengineering.baraudio.messageMaxSize
import com.sommerengineering.baraudio.origin
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.util.Objects

fun listenToDatabaseWrites(
    messages: SnapshotStateList<Message>,
    listState: LazyListState,
    coroutineScope: CoroutineScope
) {

    // triggers once for every child on initial connection
    dbRef.addChildEventListener(object : ChildEventListener {

        override fun onChildAdded(
            snapshot: DataSnapshot,
            previousChildName: String?) {

            // extract attributes
            val timestamp = snapshot.key
            val messageJson = Objects.toString(snapshot.value, "")

            if (timestamp.isNullOrEmpty() || messageJson.isEmpty()) return

            // parse json
            val json = JSONObject(messageJson)
            val message = json.getString(message)
            val imageId = getOriginImageId(json.getString(origin))

            // todo observe a State<LinkedList> to reverse order efficiently
            messages.add(0, Message(timestamp, message, imageId))
            coroutineScope.launch { listState.scrollToItem(0) }

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
    messages: SnapshotStateList<Message>
) {

    dbRef.removeValue()
    messages.clear()
}