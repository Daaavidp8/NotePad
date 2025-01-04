package com.davpicroc.notepad.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.davpicroc.notepad.entity.NoteEntity

@Dao
interface NoteDao {
    @Query("SELECT * FROM NoteEntity WHERE userId == :id ORDER BY isPinned DESC, id ASC")
    fun getAllNotesFromUser(id: Long): MutableList<NoteEntity>
    @Query("SELECT * FROM NoteEntity WHERE id = :id")
    fun getNote(id: Long): NoteEntity
    @Insert
    fun addNote(noteEntity: NoteEntity)
    @Update
    fun updateNote(noteEntity: NoteEntity)
    @Delete
    fun deleteNote(noteEntity: NoteEntity)
}