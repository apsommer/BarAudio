package com.sommerengineering.baraudio.messages

import androidx.compose.foundation.Image
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.foundation.lazy.items
import androidx.compose.foundation.lazy.rememberLazyListState
import androidx.compose.material3.CenterAlignedTopAppBar
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.snapshots.SnapshotStateList
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.res.painterResource
import androidx.compose.ui.unit.dp
import com.google.firebase.auth.ktx.auth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.sommerengineering.baraudio.MainActivity
import com.sommerengineering.baraudio.R
import com.sommerengineering.baraudio.databaseUrl
import com.sommerengineering.baraudio.message
import com.sommerengineering.baraudio.messages
import com.sommerengineering.baraudio.origin
import com.sommerengineering.baraudio.unauthenticatedUser
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.delay
import kotlinx.coroutines.launch
import org.json.JSONObject
import java.util.Objects

@Composable
fun MessagesScreen(
    onSettingsClick: () -> Unit) {

    // request notification permission, does nothing if already granted
    (LocalContext.current as MainActivity).requestRealtimeNotificationPermission()

    // init
    val messages = remember { mutableStateListOf<Message>() }
    val listState = rememberLazyListState()
    val coroutineScope = rememberCoroutineScope()

    // listen to database writes
    LaunchedEffect(databaseUrl) {

        // todo dev: launch to settings
        coroutineScope.launch {
            delay(100)
            onSettingsClick.invoke()
        }

        listenToDatabaseWrites(
            messages,
            listState,
            coroutineScope)
    }

    Scaffold(
        topBar = {
            MessagesTopBar(
                onSettingsClick,
                messages)
        }) { scaffoldPadding ->

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
            LazyColumn(
                state = listState) {
                items(
                    messages,
                    key = { it.timestamp }) {
                    MessageItem(it, Modifier.animateItem())
                }
            }
        }
    }
}

// todo do this without experimental optin
@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MessagesTopBar(
    onSettingsClick: () -> Unit,
    messages: SnapshotStateList<Message>) {

    return CenterAlignedTopAppBar(
        modifier = Modifier.padding(start = 8.dp),
        navigationIcon = {
            IconButton(
                onClick = { deleteDatabaseMessages(messages) },
                enabled = !messages.isEmpty()) {
                Icon(
                    painter = painterResource(R.drawable.sweep),
                    contentDescription = null)
            }
        },
        title = {
            Image(
                modifier = Modifier
                    .padding(8.dp),
                painter = painterResource(R.drawable.logo_banner),
                contentDescription = null)
        },
        actions = {
            IconButton(
                onClick = { onSettingsClick() }) {
                Icon(
                    painter = painterResource(R.drawable.more_vertical),
                    contentDescription = null)
            }
        })
}

val dbRef by lazy {
    val uid = Firebase.auth.currentUser?.uid ?: unauthenticatedUser
    val db = Firebase.database(databaseUrl)
    db.getReference(messages).child(uid)
}

fun listenToDatabaseWrites(
    messages: SnapshotStateList<Message>,
    listState: LazyListState,
    coroutineScope: CoroutineScope) {

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
            if (messages.size > 100) {
                messages.removeAt(100)
                // todo delete on backend as well
            }
        }

        // do nothing
        override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) { }
        override fun onChildRemoved(snapshot: DataSnapshot) { }
        override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) { }
        override fun onCancelled(error: DatabaseError) { }
    })
}

fun deleteDatabaseMessages(
    messages: SnapshotStateList<Message>) {

    dbRef.removeValue()
    messages.clear()
}
