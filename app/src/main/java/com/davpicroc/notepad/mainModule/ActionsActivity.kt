package com.davpicroc.notepad.mainModule

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.util.Log
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.davpicroc.notepad.NoteApplication
import com.davpicroc.notepad.R
import com.davpicroc.notepad.databinding.ActivityActionsBinding
import com.davpicroc.notepad.entity.NoteEntity
import com.davpicroc.notepad.mainModule.adapter.NoteAdapter
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.properties.Delegates

@RequiresApi(Build.VERSION_CODES.O)
class ActionsActivity : AppCompatActivity() {


    private lateinit var noteAdapter: NoteAdapter
    private lateinit var abinding: ActivityActionsBinding

    private val preferences by lazy { getSharedPreferences("MyPreferences", Context.MODE_PRIVATE) }
    private val lastUserIdKey by lazy { getString(R.string.sp_last_user) }
    private val note by lazy { intent.getStringExtra("Note")?.let { NoteEntity.fromString(it) } }
    private var userId by Delegates.notNull<Long>()

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        abinding = ActivityActionsBinding.inflate(layoutInflater)
        setContentView(abinding.root)





        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        loadUserId()

        loadDataNote()

        abinding.buttonBack.setOnClickListener {
            finish()
        }

        abinding.buttonOk.setOnClickListener {
            if (note == null){
                addNote()
            }else{
                updateNote()
            }
            finish()
        }
    }

    private fun addNote() {
        val newNote = NoteEntity(
            Title = abinding.editTextTitle.text.toString(),
            Content = abinding.contentNote.text.toString(),
            Date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yy")),
            userId = userId)
        Thread{
            NoteApplication.database.noteDao().addNote(newNote)
        }.start()
        noteAdapter.add(newNote)
    }

    private fun updateNote() {
        if (note?.Title != abinding.editTextTitle.text.toString() || note?.Content != abinding.contentNote.text.toString()){
            val updatedNote = note?.let {
                NoteEntity(
                    Title = abinding.editTextTitle.text.toString(),
                    Content = abinding.contentNote.text.toString(),
                    Date = it.Date,
                    userId = it.userId
                )
            }
            Thread{
                NoteApplication.database.noteDao().updateNote(updatedNote!!)
            }.start()
            noteAdapter.update(updatedNote!!)
        }
    }

    private fun loadUserId() {
        userId = preferences.getLong(lastUserIdKey, 0)
    }



    private fun loadDataNote() {
        abinding.editTextTitle.setText(note?.Title)
        abinding.contentNote.setText(note?.Content)
    }


}