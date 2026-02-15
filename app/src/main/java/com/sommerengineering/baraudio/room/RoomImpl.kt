package com.sommerengineering.baraudio.room

import com.sommerengineering.baraudio.ApplicationScope
import com.sommerengineering.baraudio.messages.Message
import com.sommerengineering.baraudio.messagesMaxSize
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.flow.map
import kotlinx.coroutines.launch
import javax.inject.Inject

class RoomImpl @Inject constructor(
    @ApplicationScope private val appScope: CoroutineScope,
    private val dao: MessageDao) {

    val messages = dao
        .observeMessages()
        .map { entities -> entities.map { it.toMessage() } }

    fun addMessage(message: Message) =
        appScope.launch {
            dao.insert(message.toEntity())
            dao.trimToLast(messagesMaxSize)
        }

    fun deleteMessage(message: Message) =
        appScope.launch {
            dao.delete(message.toEntity())
        }

    fun deleteAllMessages() =
        appScope.launch {
            dao.deleteAll()
        }
}