package com.davpicroc.notepad.editNoteModule.viewModel

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.davpicroc.notepad.common.entities.UserEntity
import com.davpicroc.notepad.editNoteModule.model.EditUserInteractor
import kotlinx.coroutines.launch

class EditUserViewModel : ViewModel() {

    private val editUserInteractor: EditUserInteractor = EditUserInteractor()

    fun saveUser(userEntity: UserEntity) {
        viewModelScope.launch {
            editUserInteractor.saveUser(userEntity) { id ->
            }
        }
    }
}
