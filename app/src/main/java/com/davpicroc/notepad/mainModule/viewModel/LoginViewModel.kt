package com.davpicroc.notepad.mainModule.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.davpicroc.notepad.NoteApplication
import com.davpicroc.notepad.common.entities.UserEntity
import com.davpicroc.notepad.mainModule.model.LoginInteractor
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext

class LoginViewModel : ViewModel() {

    private val mainInteractor: LoginInteractor = LoginInteractor()

    fun getUserById(id: Long): UserEntity? {
        return runBlocking {
            mainInteractor.getUserById(id)
        }
    }

    suspend fun getUserByName(name: String): UserEntity {
        return withContext(Dispatchers.IO) {
            NoteApplication.database.userDao().getUserByName(name)
        }
    }

    private val users: MutableLiveData<List<UserEntity>> by lazy {
        MutableLiveData<List<UserEntity>>().also {
            loadUsers()
        }
    }

    private fun loadUsers() {
        mainInteractor.getUsers() { users ->
            this.users.value = users
        }
    }
}
