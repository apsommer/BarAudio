package com.sommerengineering.baraudio.alerts

import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.sommerengineering.baraudio.MainActivity
import com.sommerengineering.baraudio.databaseUrl
import com.sommerengineering.baraudio.logException
import com.sommerengineering.baraudio.logMessage
import org.koin.java.KoinJavaComponent.inject

sealed class AlertsState {
    object Loading : AlertsState()
    data class Success(val alerts: List<String>) : AlertsState()
    data class Error(val message: String) : AlertsState()
}

@Composable
fun AlertsScreen(
    modifier: Modifier = Modifier) {

    // todo show some ui explaining permission request?
    (LocalContext.current as MainActivity).requestRealtimeNotificationPermission()

    listenToDatabaseWrites()

    Scaffold(
        topBar = {
            // todo profile image top right
        },
        floatingActionButton = {
            // todo plus button
        }
    ) { padding ->

        LazyColumn(Modifier.padding(padding)) {
            items(getAlerts()) { alert ->
                AlertItem(alert)
                HorizontalDivider()
            }
        }
    }
}

fun listenToDatabaseWrites() {

    // get user id
    val firebaseAuth: FirebaseAuth by inject(FirebaseAuth::class.java)
    val uid = firebaseAuth.currentUser?.uid ?: return

    // get reference to database
    val db = Firebase.database(databaseUrl)
    val uidKey = db.getReference("messages").child(uid)

    logMessage(uidKey.toString())

    // listen to new message database writes
    uidKey.addChildEventListener(object : ChildEventListener {

        override fun onChildAdded(
            snapshot: DataSnapshot,
            previousChildName: String?) {

            logMessage("Firebase realtime database, onChildAdded: $previousChildName")
        }

        // do nothing
        override fun onChildChanged(snapshot: DataSnapshot, previousChildName: String?) {

            logMessage("Firebase realtime database, onChildAdded: $previousChildName")
        }
        override fun onChildRemoved(snapshot: DataSnapshot) { }
        override fun onChildMoved(snapshot: DataSnapshot, previousChildName: String?) { }
        override fun onCancelled(error: DatabaseError) { }
    })
}

////////////////////////////////////////////////////////////////////////////////////////////////////
