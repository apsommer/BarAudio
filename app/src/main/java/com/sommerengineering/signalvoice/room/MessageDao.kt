package com.sommerengineering.signalvoice.room

import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import androidx.room.Transaction
import kotlinx.coroutines.flow.Flow

@Dao
interface MessageDao {

    @Query("SELECT * FROM messages ORDER BY timestamp DESC")
    fun observeMessages(): Flow<List<MessageEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: MessageEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertAll(entities: List<MessageEntity>)

    @Query("DELETE FROM messages")
    suspend fun deleteAll()

    @Transaction // atomic, prevents ui from receiving empty list
    suspend fun replaceAll(entities: List<MessageEntity>) {
        deleteAll()
        insertAll(entities)
    }

    @Transaction // atomic, prevents ui from receiving empty list
    suspend fun replaceStream(stream: String, entities: List<MessageEntity>) {
        deleteStream(stream)
        insertAll(entities)
    }

    @Query("DELETE FROM messages WHERE stream = :stream")
    suspend fun deleteStream(stream: String)


}