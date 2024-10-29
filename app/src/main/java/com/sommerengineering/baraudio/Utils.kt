package com.sommerengineering.baraudio

import android.util.Log

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
