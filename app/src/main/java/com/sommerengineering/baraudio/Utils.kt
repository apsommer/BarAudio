package com.sommerengineering.baraudio

import android.util.Log

fun handleException(e: Exception) {
    Log.e(TAG, "handleException: ", e)
}

fun logMessage(msg: String) {
    Log.e(TAG, "logMessage: $msg")
}