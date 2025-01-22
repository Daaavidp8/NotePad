package com.davpicroc.notepad.mainModule.model

import com.davpicroc.notepad.NoteApplication
import com.davpicroc.notepad.common.entities.NoteEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class MainInteractor {
    fun getNotes(id: Long,callback: (MutableList<NoteEntity>) -> Unit){
        CoroutineScope(Dispatchers.IO).launch{
            val notesList = NoteApplication.database.noteDao().getAllNotesFromUser(id)
            withContext(Dispatchers.Main){
                callback(notesList)
            }
        }
    }
}