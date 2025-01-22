package com.davpicroc.notepad.mainModule.adapter

import com.davpicroc.notepad.common.entities.NoteEntity

interface OnClickListener {
    fun onClick(note: NoteEntity)
    fun onLongClick(note: NoteEntity)
}