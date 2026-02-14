package com.sommerengineering.baraudio.hilt

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.sommerengineering.baraudio.messages.Message

@Entity(tableName = "messages")
data class MessageTable(
    @PrimaryKey val timestamp: String,
    val message: String,
    val origin: String
) {

    fun toMessage() = Message(timestamp, message, origin)

    companion object {
        fun from(message: Message) =
            MessageTable(message.timestamp, message.message, message.origin)
    }
}