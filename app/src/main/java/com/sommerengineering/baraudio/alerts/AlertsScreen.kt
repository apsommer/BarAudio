package com.sommerengineering.baraudio.alerts

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.sommerengineering.baraudio.MainActivity
import com.sommerengineering.baraudio.databaseUrl
import java.util.Objects

@Composable
fun AlertsScreen(
    modifier: Modifier = Modifier) {

    // todo show some ui explaining permission request?
    (LocalContext.current as MainActivity).requestRealtimeNotificationPermission()

    val messages = remember { mutableStateListOf<Message>() }
    listenToDatabaseWrites(messages)

    Scaffold(
        topBar = {
            // todo profile image top right
        },
        floatingActionButton = {
            // todo plus button
        }
    ) { padding ->

        LazyColumn(Modifier.padding(padding)) {
            items(messages) { alert ->
                MessageItem(alert)
                HorizontalDivider()
            }
        }
    }
}

fun listenToDatabaseWrites(
    messages: SnapshotStateList<Message>
) {

    // get user id
    val uid = Firebase.auth.currentUser?.uid ?: return

    // get reference to database
    val db = Firebase.database(databaseUrl)
    val uidKey = db.getReference("messages").child(uid)

    // listen to new message database writes
    // triggers once for every child on initial connection
    uidKey.addChildEventListener(object : ChildEventListener {

        override fun onChildAdded(
            snapshot: DataSnapshot,
            previousChildName: String?) {

            // extract attributes
            val timestamp = snapshot.key
            val message = Objects.toString(snapshot.value, "")

            if (timestamp.isNullOrEmpty() || message.isEmpty()) return

            messages.add(
                Message(
                    timestamp,
                    message))

        }

        // do nothing
        override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) { }
        override fun onChildRemoved(snapshot: DataSnapshot) { }
        override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) { }
        override fun onCancelled(error: DatabaseError) { }
    })
}

////////////////////////////////////////////////////////////////////////////////////////////////////
