package com.davpicroc.notepad.mainModule.model

import com.davpicroc.notepad.NoteApplication
import com.davpicroc.notepad.common.entities.NoteEntity
import com.davpicroc.notepad.common.entities.UserEntity
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext

class LoginInteractor {
    fun getUsers(callback: (MutableList<UserEntity>) -> Unit){
        CoroutineScope(Dispatchers.IO).launch{
            val users = NoteApplication.database.userDao().getAllUsers()
            withContext(Dispatchers.Main){
                callback(users)
            }
        }
    }

    suspend fun getUserById(id: Long): UserEntity? {
        return withContext(Dispatchers.IO) {
            NoteApplication.database.userDao().getUserById(id)
        }
    }

    suspend fun getUserByName(name: String): UserEntity?{
        return withContext(Dispatchers.IO) {
            NoteApplication.database.userDao().getUserByName(name)
        }
    }
}