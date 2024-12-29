package com.sommerengineering.baraudio

import android.content.Context
import android.text.format.DateUtils
import android.util.Log
import androidx.datastore.preferences.core.edit
import androidx.datastore.preferences.core.stringPreferencesKey
import com.google.firebase.crashlytics.ktx.crashlytics
import com.google.firebase.ktx.Firebase
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.flow.first
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// logs
const val TAG = "~"

fun logMessage(msg: String?) =
    Log.v(TAG, "$msg")

fun logException(e: Exception) {
    Log.e(TAG, "handleException: ${e.message}", e)
    Firebase.crashlytics.recordException(e)
}

fun beautifyTimestamp(
    timestamp: String
): String {

    val isToday = DateUtils.isToday(timestamp.toLong())

    val pattern =
        if (isToday) "h:mm:ss a" // 6:27:53 PM
        else "MMMM dd, yyyy • h:mm:ss a" //  October 30, 2024 • 6:27:53 PM

    return SimpleDateFormat(
        pattern,
        Locale.getDefault())
        .format(Date(timestamp.toLong()))
}

fun readFromDataStore(
    context: Context,
    key: String) =
        runBlocking {
            context.dataStore.data
                .map { it[stringPreferencesKey(key)] }
                .first()
        }

fun writeToDataStore(
    context: Context,
    key: String,
    value: String) =
        CoroutineScope(Dispatchers.IO).launch {
            context.dataStore.edit {
                it[stringPreferencesKey(key)] = value
            }
        }
