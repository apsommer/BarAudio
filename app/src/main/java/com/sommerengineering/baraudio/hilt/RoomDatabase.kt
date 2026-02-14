package com.sommerengineering.baraudio.hilt

import androidx.room.Dao
import androidx.room.Database
import androidx.room.Entity
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.PrimaryKey
import androidx.room.Query
import androidx.room.RoomDatabase
import com.sommerengineering.baraudio.messages.Message
import kotlinx.coroutines.flow.Flow

@Entity(tableName = "messages")
data class MessageEntity(
    @PrimaryKey val timestamp: String,
    val message: String,
    val origin: String)

fun MessageEntity.toMessage() = Message(timestamp, message, origin)
fun Message.toEntity() = MessageEntity(timestamp, message, origin)

@Dao
interface MessageDao {

    @Query(""" SELECT * FROM messages ORDER BY timestamp DESC LIMIT :limit """)
    fun observeMessages(limit: Int): Flow<List<MessageEntity>>

    @Insert
    suspend fun insert(entity: MessageEntity)

    @Query(""" DELETE FROM messages WHERE timestamp
        NOT IN (SELECT timestamp FROM messages ORDER BY timestamp DESC LIMIT :limit) """)
    suspend fun trimToLast(limit: Int)

    @Query("DELETE FROM messages")
    suspend fun deleteAll()
}

@Database(
    entities = [MessageEntity::class],
    version = 1,
    exportSchema = false) // todo true for production and migration strategy
abstract class MessageDatabase : RoomDatabase() {
    abstract fun messageDao(): MessageDao
}


