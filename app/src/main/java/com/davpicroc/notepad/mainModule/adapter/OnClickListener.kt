package com.davpicroc.notepad.mainModule.adapter

import com.davpicroc.notepad.entity.NoteEntity

interface OnClickListener {
    fun onClick(note: NoteEntity, position: Int)
    fun onLongClick(note: NoteEntity)
}