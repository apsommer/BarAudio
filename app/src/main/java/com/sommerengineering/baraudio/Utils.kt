package com.sommerengineering.baraudio

import android.text.format.DateUtils
import android.util.Log
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

// logcat
const val TAG = "~"
fun logException(e: Exception?) = Log.e(TAG, "handleException: ${e?.message}", e)
fun logMessage(msg: String?) = Log.v(TAG, "$msg")

// firebase
const val databaseUrl = "https://com-sommerengineering-baraudio-default-rtdb.firebaseio.com/"
const val webhookBaseUrl = "https://baraudio-555667494303.us-central1.run.app/?uid="
const val aboutUrl = "https://baraud.io/"
const val privacyUrl = "https://baraud.io/privacy/"
const val termsUrl = "https://baraud.io/terms/"
const val users = "users"

//
const val isLaunchFromNotification = "isLaunchFromNotification"

fun beautifyTimestamp(timestamp: String): String {

    // todo handle local change in system system while app running
    //  https://stackoverflow.com/a/23556454/9212084

    // todo relative timespan is interesting?
    //  DateUtils.getRelativeTimeSpanString(timestamp.toLong())

    val isToday = DateUtils.isToday(timestamp.toLong())

    val pattern =
        if (isToday) "h:mm:ss a" // 6:27:53 PM
        else "h:mm:ss a 'on' MMMM dd, yyyy" // 6:27:53 PM on October 30, 2024

    return SimpleDateFormat(
        pattern,
        Locale.getDefault())
        .format(Date(timestamp.toLong()))
}

fun trimTimestamp(timestamp: String) =
    timestamp
        .substring(timestamp.length - 9, timestamp.length)
        .toInt()