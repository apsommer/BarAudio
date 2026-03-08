package com.sommerengineering.baraudio.uitls

import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object TimestampFormatter {

    private const val minute = 60_000L
    private const val hour = 60 * minute
    private const val day = 24 * hour
    private val clockFormat = SimpleDateFormat("HH:mm", Locale.getDefault()) // 14:32
    private val weekdayFormat = SimpleDateFormat("EEE", Locale.getDefault()) // Mon
    private val dateFormat = SimpleDateFormat("MMM d", Locale.getDefault()) // Oct 30
    private val fullFormat = SimpleDateFormat("EEEE, MMMM d, yyyy • HH:mm:ss", Locale.getDefault())

    fun beautifyCompact(timestamp: String): String {

        val time = timestamp.toLong()
        val now = System.currentTimeMillis()
        val diff = now - time
        val date = Date(time)

        return when {
            minute > diff -> "just now ${clockFormat.format(date)}"
            hour > diff -> "${diff / minute}m ago ${clockFormat.format(date)}"
            day > diff -> "${diff / hour}h ago ${clockFormat.format(date)}"
            day * 2 > diff -> "Yesterday ${clockFormat.format(date)}"
            day * 4 > diff -> "${weekdayFormat.format(date)} ${clockFormat.format(date)}"
            else -> "${dateFormat.format(date)} • ${clockFormat.format(date)}"
        }
    }

    fun beautifyFull(timestamp: String) =
        fullFormat.format(Date(timestamp.toLong()))
}