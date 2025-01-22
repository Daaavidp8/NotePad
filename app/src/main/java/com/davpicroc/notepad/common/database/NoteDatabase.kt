package com.davpicroc.notepad.common.database

import androidx.room.Database
import androidx.room.RoomDatabase
import com.davpicroc.notepad.common.entities.NoteEntity
import com.davpicroc.notepad.common.entities.UserEntity

@Database(entities = [NoteEntity::class, UserEntity::class], version = 2, exportSchema = false)
abstract class NoteDatabase : RoomDatabase() {
    abstract fun noteDao(): NoteDao
    abstract fun userDao(): UserDao
}
