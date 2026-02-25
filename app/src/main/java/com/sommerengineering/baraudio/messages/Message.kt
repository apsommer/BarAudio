package com.sommerengineering.baraudio.messages

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Message(
    val timestamp: String,
    val message: String,
    val stream: String?, // null for user signal
    val source: String? // null for stream broadcast
) : Parcelable