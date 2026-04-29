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

    suspend fun addMessages(messages: List<Message>) =
        dao.insertAll(messages.map { it.toEntity() })

    suspend fun replaceMessages(messages: List<Message>) =
        dao.replaceAll(messages.map { it.toEntity() })

    suspend fun replaceStream(stream: String, messages: List<Message>) =
        dao.replaceStream(stream, messages.map { it.toEntity() })

    suspend fun removeStream(stream: String) =
        dao.deleteStream(stream)

    suspend fun replaceUserMessages(messages: List<Message>) =
        dao.replaceUserMessages(messages.map { it.toEntity() })

    suspend fun removeUserMessages() =
        dao.deleteUserMessages()
}