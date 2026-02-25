package com.sommerengineering.baraudio.room

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.sommerengineering.baraudio.messages.Message

@Entity(tableName = "messages")
data class MessageEntity(
    @PrimaryKey val timestamp: String,
    val message: String,
    val stream: String?, // null for user signal
    val sourceIp: String? // null for stream broadcast
)

fun MessageEntity.toMessage() = Message(timestamp, message, stream, sourceIp)
fun Message.toEntity() = MessageEntity(timestamp, message, stream, source)