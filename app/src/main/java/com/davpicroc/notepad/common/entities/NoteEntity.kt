package com.davpicroc.notepad.common.entities

import android.os.Build
import androidx.annotation.RequiresApi
import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "NoteEntity")
data class NoteEntity @RequiresApi(Build.VERSION_CODES.O) constructor(
    @PrimaryKey(autoGenerate = true) var id: Long = 0,
    var Title: String,
    var Content: String = "",
    var Date: String,
    var isPinned: Boolean = false,
    var userId: Long
){
    companion object{
        @RequiresApi(Build.VERSION_CODES.O)
        fun fromString(string: String): NoteEntity? {
            val regex = """id=(\d+), Title=([A-Za-z0-9\s]+), Content=([A-Za-z0-9\s]+), Date=([0-9/]+), isPinned=(true|false), userId=(\d+)""".toRegex()

            val matchResult = regex.find(string)
            return matchResult?.let {
                val (id, title, content, date, isPinned, userId) = it.destructured
                NoteEntity(
                    id = id.toLong(),
                    Title = title,
                    Content = content,
                    Date = date,
                    isPinned = isPinned.toBoolean(),
                    userId = userId.toLong()
                )
            }
        }
    }
}
