package com.davpicroc.notepad.entity

data class NoteEntity(
    var id: Long = 0,
    var Title: String,
    var Content: String = "",
    var Date: String,
    var isPinned: Boolean = false
)
