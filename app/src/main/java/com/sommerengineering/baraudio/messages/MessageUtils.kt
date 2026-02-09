package com.sommerengineering.baraudio.messages

import android.text.format.DateUtils
import androidx.compose.foundation.lazy.LazyListState
import androidx.compose.runtime.snapshots.SnapshotStateList
import com.google.firebase.database.ChildEventListener
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.sommerengineering.baraudio.hilt.getDatabaseReference
import com.sommerengineering.baraudio.logException
import com.sommerengineering.baraudio.logMessage
import com.sommerengineering.baraudio.messageKey
import com.sommerengineering.baraudio.messageMaxSize
import com.sommerengineering.baraudio.messagesNode
import com.sommerengineering.baraudio.originKey
import com.sommerengineering.baraudio.parsingError
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.launch
import org.json.JSONException
import org.json.JSONObject
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale



fun beautifyTimestamp(
    timestamp: String): String {

    val isToday = DateUtils.isToday(timestamp.toLong())

    val pattern =
        if (isToday) "h:mm:ss a" // 6:27:53 PM
        else "h:mm:ss a • MMMM dd, yyyy" //  6:27:53 PM • October 30, 2024

    return SimpleDateFormat(
        pattern,
        Locale.getDefault())
        .format(Date(timestamp.toLong()))
}