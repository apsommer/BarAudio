package com.sommerengineering.baraudio.messages

import android.os.Parcelable
import kotlinx.parcelize.Parcelize

@Parcelize
data class Message(
    var timestamp: String,
    var message: String,
    var origin: String
) : Parcelable