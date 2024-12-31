package com.davpicroc.notepad.mainModule

import android.os.Build
import android.os.Bundle
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

class ActionsActivity : AppCompatActivity() {


    private lateinit var noteAdapter: NoteAdapter
    private lateinit var abinding: ActivityActionsBinding

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

        abinding.buttonBack.setOnClickListener {
            finish()
        }

        abinding.buttonOk.setOnClickListener {
            val note = NoteEntity(
                Title = abinding.editTextTitle.text.toString(),
                Content = abinding.contentNote.text.toString(),
                Date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yy")))

            Thread{
                NoteApplication.database.noteDao().addNote(note)
            }.start()

            noteAdapter.add(note)
        }
    }


}