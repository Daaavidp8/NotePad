package com.davpicroc.notepad.mainModule.viewModel

import android.content.Context
import android.provider.ContactsContract.CommonDataKinds.Note
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.davpicroc.notepad.R
import com.davpicroc.notepad.common.entities.NoteEntity
import com.davpicroc.notepad.mainModule.model.MainInteractor
import kotlin.properties.Delegates

class MainViewModel(private val userId: Long): ViewModel() {
    private var noteList: MutableList<NoteEntity>
    private var mainInteractor: MainInteractor


    init {
        noteList = mutableListOf()
        mainInteractor = MainInteractor()
    }

    fun getNotes(): LiveData<List<NoteEntity>>{
        return notes
    }

    private val notes: MutableLiveData<List<NoteEntity>> by lazy {
        MutableLiveData<List<NoteEntity>>().also {
            loadNotes()
        }
    }

    private fun loadNotes(){
        mainInteractor.getNotes(userId){
            notes.value = it
            noteList = it
        }
    }



}

