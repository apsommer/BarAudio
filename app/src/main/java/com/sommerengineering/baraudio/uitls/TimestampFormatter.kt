package com.sommerengineering.baraudio.uitls

import android.text.format.DateUtils
import java.text.SimpleDateFormat
import java.util.Date
import java.util.Locale

object TimestampFormatter {

    // 6:27:53 PM
    private val todayFormat =
        SimpleDateFormat("h:mm:ss a", Locale.getDefault())

    // 6:27:53 PM • October 30, 2024
    private val format =
        SimpleDateFormat("h:mm:ss a • MMMM dd, yyyy", Locale.getDefault())

    fun beautify(timestamp: String): String {

        val date = Date(timestamp.toLong())
        val isToday = DateUtils.isToday(timestamp.toLong())

        return if (isToday) todayFormat.format(date) else format.format(date)
    }
}