package com.davpicroc.notepad

import android.app.Application
import androidx.room.Room
import com.davpicroc.notepad.dao.NoteDatabase

class NoteApplication: Application() {
    companion object {
        lateinit var database: NoteDatabase
    }
    override fun onCreate() {
        super.onCreate()
        database = Room.databaseBuilder(
            this,
            NoteDatabase::class.java,
            "NoteDatabase")
            .build()
    }
}