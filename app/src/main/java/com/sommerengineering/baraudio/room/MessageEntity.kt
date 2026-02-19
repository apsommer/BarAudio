package com.sommerengineering.baraudio.room

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.sommerengineering.baraudio.messages.Message

@Entity(tableName = "messages")
data class MessageEntity(
    @PrimaryKey val timestamp: String,
    val message: String,
    val origin: String
)

fun MessageEntity.toMessage() = Message(timestamp, message, origin)
fun Message.toEntity() = MessageEntity(timestamp, message, origin)