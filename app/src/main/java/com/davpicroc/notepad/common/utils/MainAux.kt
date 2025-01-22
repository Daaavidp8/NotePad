package com.davpicroc.notepad.common.utils

import com.davpicroc.notepad.common.entities.NoteEntity

interface MainAux {
    fun hideFab(isVisible: Boolean = false)
    fun addNote(noteEntity: NoteEntity)
    fun updateNote(noteEntity: NoteEntity)
}