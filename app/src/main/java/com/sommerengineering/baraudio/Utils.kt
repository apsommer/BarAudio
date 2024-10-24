package com.sommerengineering.baraudio

import android.util.Log

// debug logcat
const val TAG = "~"

fun logException(e: Exception?) =
    Log.e(TAG, "handleException: ", e)

fun logMessage(msg: String?) =
    Log.v(TAG, "$msg")
