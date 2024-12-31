package com.davpicroc.notepad.dao

import androidx.room.Database
import androidx.room.RoomDatabase
import com.davpicroc.notepad.entity.NoteEntity

@Database(entities = [NoteEntity::class], version = 1)
abstract class NoteDatabase : RoomDatabase() {
    abstract fun noteDao(): NoteDao
}