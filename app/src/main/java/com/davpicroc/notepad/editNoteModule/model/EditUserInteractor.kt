package com.davpicroc.notepad.editNoteModule.model

import com.davpicroc.notepad.NoteApplication
import com.davpicroc.notepad.common.entities.UserEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class EditUserInteractor {
    suspend fun saveUser(userEntity: UserEntity, callback: (Long) -> Unit) {
        try {
            val id = NoteApplication.database.userDao().addUser(userEntity)
            callback(id)
        } catch (e: Exception) {
            throw Exception("Error al guardar el usuario: ${e.localizedMessage}")
        }
    }
}
