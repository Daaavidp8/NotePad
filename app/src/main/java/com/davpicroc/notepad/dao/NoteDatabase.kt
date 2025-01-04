package com.davpicroc.notepad.dao

import androidx.room.Database
import androidx.room.RoomDatabase
import com.davpicroc.notepad.entity.NoteEntity
import com.davpicroc.notepad.entity.UserEntity

@Database(entities = [NoteEntity::class, UserEntity::class], version = 2, exportSchema = false)
abstract class NoteDatabase : RoomDatabase() {
    abstract fun noteDao(): NoteDao
    abstract fun userDao(): UserDao
}
