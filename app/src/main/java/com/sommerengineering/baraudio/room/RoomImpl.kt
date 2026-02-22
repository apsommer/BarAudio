package com.sommerengineering.baraudio.room

import com.sommerengineering.baraudio.messages.Message
import kotlinx.coroutines.flow.map
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class RoomImpl @Inject constructor(
    private val dao: MessageDao) {

    val messages = dao
        .observeMessages()
        .map { entities -> entities.map { it.toMessage() } }

    suspend fun addMessage(message: Message) =
        dao.insert(message.toEntity())

    suspend fun addMessages(messages: List<Message>) =
        dao.insertAll(messages.map { it.toEntity() })
}