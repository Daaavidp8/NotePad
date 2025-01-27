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

    fun getUserByName(name: String,callback: (UserEntity) -> Unit){
        CoroutineScope(Dispatchers.IO).launch{
            val user = NoteApplication.database.userDao().getUserByName(name)
            withContext(Dispatchers.Main){
                callback(user)
            }
        }
    }
}