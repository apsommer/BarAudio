package com.sommerengineering.baraudio.room

import androidx.room.Database
import androidx.room.RoomDatabase

@Database(
    entities = [MessageEntity::class],
    version = 1,
    exportSchema = false) // todo true for production, plus create migration strategy
abstract class MessageDatabase : RoomDatabase() {
    abstract fun messageDao(): MessageDao
}