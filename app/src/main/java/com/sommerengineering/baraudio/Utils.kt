package com.sommerengineering.baraudio

import android.util.Log

// debug logcat
const val TAG = "~"

// firebase
const val databaseUrl = "https://com-sommerengineering-baraudio.firebaseio.com/"
const val users = "users"

fun logException(e: Exception?) =
    Log.e(TAG, "handleException: ", e)

fun logMessage(msg: String?) =
    Log.v(TAG, "$msg")
