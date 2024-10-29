package com.sommerengineering.baraudio.alerts

import android.content.res.Configuration.UI_MODE_NIGHT_NO
import android.content.res.Configuration.UI_MODE_NIGHT_YES
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Scaffold
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.tooling.preview.Preview
import com.google.firebase.auth.FirebaseAuth
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.ValueEventListener
import com.google.firebase.database.ktx.database
import com.google.firebase.ktx.Firebase
import com.sommerengineering.baraudio.MainActivity
import com.sommerengineering.baraudio.logException
import com.sommerengineering.baraudio.logMessage
import com.sommerengineering.baraudio.theme.AppTheme
import org.koin.java.KoinJavaComponent.inject

sealed class AlertsState {
    object Loading : AlertsState()
    data class Success(val alerts: List<String>) : AlertsState()
    data class Error(val message: String) : AlertsState()
}

@Composable
fun AlertsScreen(
    modifier: Modifier = Modifier) {

    // todo debug test
    (LocalContext.current as MainActivity).requestRealtimeNotificationPermission()

    // todo check that notifications have appropriate settings:
    //  importance, sound, etc at minimum levels, else show ui saying it's required
    //  also put link to system settings somewhere appropriate
    //  https://developer.android.com/develop/ui/views/notifications/channels#UpdateChannel

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
    val urlString = "https://com-sommerengineering-baraudio.firebaseio.com/"
    val db = Firebase.database(urlString)
    val messagesKey = db.getReference("messages").child(uid)

    // listen to new message database writes
    messagesKey.addValueEventListener(object : ValueEventListener {

        override fun onDataChange(snapshot: DataSnapshot) {
            val value = snapshot.getValue()
            logMessage("Firebase realtime database, onDataChange: $messagesKey: $value")
        }

        override fun onCancelled(error: DatabaseError) {
            logException(error.toException())
        }
    })

    // todo configure proguard for Alert pojo
    //  https://firebase.google.com/docs/database/android/start#proguard

    // todo complete launch checklist prior to production
    //  https://firebase.google.com/support/guides/launch-checklist

    // todo implement App Check via Google Play Integrity API, setup flow through firebase console
    //  https://firebase.google.com/docs/app-check/android/play-integrity-provider?hl=en&authuser=0&_gl=1*4ksu49*_ga*NTE3MjAzMTkwLjE3Mjg1NTI5MDE.*_ga_CW55HF8NVT*MTcyOTM2MTg3NS4xOC4xLjE3MjkzNjQzODIuMC4wLjA.
}

////////////////////////////////////////////////////////////////////////////////////////////////////

@Preview(
    uiMode = UI_MODE_NIGHT_YES,
    name = "dark"
)
@Preview(
    uiMode = UI_MODE_NIGHT_NO,
    name = "light"
)
@Composable
fun PreviewAlertsScreen() {
    AppTheme {
        AlertsScreen()
    }
}