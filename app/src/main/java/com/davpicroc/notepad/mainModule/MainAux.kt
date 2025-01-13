package com.davpicroc.notepad.mainModule

import com.davpicroc.notepad.entity.NoteEntity

interface MainAux {
    fun hideFab(isVisible: Boolean = false)
    fun addNote(noteEntity: NoteEntity)
    fun updateNote(noteEntity: NoteEntity)
}