package com.sommerengineering.baraudio

import android.util.Log

// debug logcat
const val TAG = "~"

fun handleException(e: Exception?) =
    Log.e(TAG, "handleException: ", e)
fun handleException(msg: String?) =
    Log.e(TAG, "handleException: $msg")

fun logMessage(msg: String?) =
    Log.v(TAG, "$msg")
