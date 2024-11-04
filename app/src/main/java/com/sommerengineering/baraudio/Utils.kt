package com.sommerengineering.baraudio

import android.util.Log
import androidx.datastore.preferences.core.stringPreferencesKey
import com.google.firebase.auth.ktx.auth
import com.google.firebase.ktx.Firebase
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// debug logcat
const val TAG = "~"

// firebase
val webhookUrl = "https://baraudio-555667494303.us-central1.run.app/?uid=${Firebase.auth.currentUser?.uid}"
const val databaseUrl = "https://com-sommerengineering-baraudio-default-rtdb.firebaseio.com/"
const val users = "users"

// preferences datastore
const val localCache = "localCache"
val tokenKey = stringPreferencesKey("token")

// todo display app-wide banner for 'no internet connection', for example sign-in currently fails silently
fun logException(e: Exception?) =
    Log.e(TAG, "handleException: ${e?.message}", e)

fun logMessage(msg: String?) =
    Log.v(TAG, "$msg")

fun beautifyTimestamp(timestamp: String): String {

    // todo handle local change in system system while app running
    //  https://stackoverflow.com/a/23556454/9212084

    val pattern = "h:mm:ss a 'on' MMMM dd, yyyy" // 6:27:53 PM on October 30, 2024

    return SimpleDateFormat(
        pattern,
        Locale.getDefault())
        .format(Date(timestamp.toLong()))
}
