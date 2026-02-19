package com.sommerengineering.baraudio.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface MessageDao {

    @Query("SELECT * FROM messages ORDER BY timestamp DESC")
    fun observeMessages(): Flow<List<MessageEntity>>

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(entity: MessageEntity)

    @Query("DELETE FROM messages WHERE timestamp NOT IN " +
            "(SELECT timestamp FROM messages ORDER BY timestamp DESC LIMIT :limit)")
    suspend fun trimToLast(limit: Int)

    @Delete
    suspend fun delete(entity: MessageEntity)

    @Query("DELETE FROM messages")
    suspend fun deleteAll()
}