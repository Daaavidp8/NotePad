package com.davpicroc.notepad.editNoteModule.viewModel

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.davpicroc.notepad.common.entities.NoteEntity
import com.davpicroc.notepad.editNoteModule.model.EditNoteInteractor

class editNoteViewModel: ViewModel() {
    private var noteSelected: MutableLiveData<NoteEntity>()
    private var showFab = MutableLiveData<Boolean>()
    private var result = MutableLiveData<Any>()

    private var editNoteInteractor: EditNoteInteractor = EditNoteInteractor()


    fun setNoteSelected(noteEntity: NoteEntity){
        noteSelected.value = noteEntity
    }

    fun getNoteSelected(): LiveData<NoteEntity>{
        return noteSelected
    }

    fun setShowFab(isVisible: Boolean){
        showFab.value = isVisible
    }

    fun getShowFab(): LiveData<Boolean>{
        return showFab
    }

    fun setResult(value: Any){
        result.value = value
    }

    fun getResult(): LiveData<Any>{
        return result
    }

    fun saveNote(noteEntity: NoteEntity){
        editNoteInteractor.saveNote(noteEntity){ newId ->
            result.value = newId
        }
    }

    fun updateNote(noteEntity: NoteEntity){
        editNoteInteractor.updateNote(noteEntity){ noteUpdated ->
            result.value = noteUpdated

        }
    }



}