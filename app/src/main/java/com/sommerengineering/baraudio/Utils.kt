package com.sommerengineering.baraudio

import android.icu.util.Calendar
import android.util.Log
import java.text.DateFormat
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// debug logcat
const val TAG = "~"

// firebase
const val databaseUrl = "https://com-sommerengineering-baraudio-default-rtdb.firebaseio.com/"
const val users = "users"

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