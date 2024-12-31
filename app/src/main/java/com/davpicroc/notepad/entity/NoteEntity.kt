package com.davpicroc.notepad.entity

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "NoteEntity")
data class NoteEntity @RequiresApi(Build.VERSION_CODES.O) constructor(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    var Title: String,
    var Content: String = "",
    var Date: String,
    var isPinned: Boolean = false
)
