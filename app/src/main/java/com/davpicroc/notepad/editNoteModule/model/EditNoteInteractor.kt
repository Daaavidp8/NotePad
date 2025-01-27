package com.davpicroc.notepad.editNoteModule.model

import com.davpicroc.notepad.NoteApplication
import com.davpicroc.notepad.common.entities.NoteEntity
import com.davpicroc.notepad.common.entities.UserEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class EditNoteInteractor {
    fun saveNote(noteEntity: NoteEntity, callback: (Long) -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            val id = NoteApplication.database.noteDao().addNote(noteEntity)
            withContext(Dispatchers.Main){
                callback(id)
            }
        }
    }

    fun updateNote(noteEntity: NoteEntity, callback: (NoteEntity) -> Unit) {
        CoroutineScope(Dispatchers.IO).launch {
            NoteApplication.database.noteDao().updateNote(noteEntity)
            withContext(Dispatchers.Main){
                callback(noteEntity)
            }
        }
    }
}