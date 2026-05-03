package com.sommerengineering.signalvoice.room

import com.sommerengineering.signalvoice.source.Message
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RoomImpl @Inject constructor(
    private val dao: MessageDao
) {

    val messages = dao
        .observeMessages()
        .map { entities -> entities.map { it.toMessage() } }

    suspend fun addMessage(message: Message) =
        dao.insert(message.toEntity())

    suspend fun replaceStreamMessages(stream: String, messages: List<Message>) =
        dao.replaceStreamMessages(stream, messages.map { it.toEntity() })

    suspend fun removeStreamMessages(stream: String) =
        dao.deleteStreamMessages(stream)

    suspend fun replaceUserMessages(messages: List<Message>) =
        dao.replaceUserMessages(messages.map { it.toEntity() })

    suspend fun removeUserMessages() =
        dao.deleteUserMessages()
}