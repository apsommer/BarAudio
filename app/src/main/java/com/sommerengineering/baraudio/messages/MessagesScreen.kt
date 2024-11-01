package com.sommerengineering.baraudio.messages

import android.content.Context
import android.util.Log
import android.widget.ImageButton
import android.widget.Toast
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Image
import androidx.compose.foundation.border
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.IconButton
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.OutlinedIconButton
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.transform.CircleCropTransformation
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.sommerengineering.baraudio.MainActivity
import com.sommerengineering.baraudio.R
import com.sommerengineering.baraudio.databaseUrl
import com.sommerengineering.baraudio.logMessage
import java.util.Objects

@Composable
fun MessagesScreen() {

    // request notification permission
    (LocalContext.current as MainActivity).requestRealtimeNotificationPermission()

    // initialize message list
    val messages = remember { mutableStateListOf<Message>() }
    listenToDatabaseWrites(messages)

    Scaffold(
        topBar = {
            TopAppBar() }) { scaffoldPadding ->

        Box(Modifier
            .fillMaxSize()
            .padding(scaffoldPadding)) {

            // background image
            Column(
                Modifier.align(Alignment.BottomCenter)) {
                Image(
                    painter = painterResource(R.drawable.background),
                    contentDescription = null)
            }

            // messages list
            LazyColumn {
                items(messages) { message ->
                    MessageItem(message)
                    HorizontalDivider()
                }
            }
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun TopAppBar() {
    return CenterAlignedTopAppBar(
        title = {
            Image(
                painterResource(R.drawable.logo_banner),
                contentDescription = null,
                modifier = Modifier.padding(8.dp)
            )
        },
        actions = {
            IconButton(
                onClick = { onClickProfileImage() }) {
                AsyncImage(
                    modifier = Modifier.clip(CircleShape),
                    model = Firebase.auth.currentUser?.photoUrl,
                    contentDescription = null)
            }
        }
    )
}

fun onClickProfileImage() {
    logMessage("Profile image click!")
}

fun listenToDatabaseWrites(
    messages: SnapshotStateList<Message>) {

    // get user id
    val uid = Firebase.auth.currentUser?.uid ?: return

    // get reference to database
    val db = Firebase.database(databaseUrl)
    val uidKey = db.getReference("messages").child(uid)
    logMessage(uidKey.toString())

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

            // todo observe a State<LinkedList> to reverse order efficiently
            messages.add(0,
                Message(timestamp, message))
        }

        // do nothing
        override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) { }
        override fun onChildRemoved(snapshot: DataSnapshot) { }
        override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) { }
        override fun onCancelled(error: DatabaseError) { }
    })
}

