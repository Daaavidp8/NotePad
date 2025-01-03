package com.davpicroc.notepad.mainModule

import android.content.Intent
import android.graphics.Canvas
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import androidx.recyclerview.widget.RecyclerView.LayoutManager
import com.davpicroc.notepad.NoteApplication
import com.davpicroc.notepad.R
import com.davpicroc.notepad.databinding.ActivityLoginBinding
import com.davpicroc.notepad.databinding.ActivityMainBinding
import com.davpicroc.notepad.entity.NoteEntity
import com.davpicroc.notepad.mainModule.adapter.NoteAdapter
import com.davpicroc.notepad.mainModule.adapter.OnClickListener

class MainActivity : AppCompatActivity(),OnClickListener {

    private lateinit var noteAdapter: NoteAdapter
    private lateinit var mLinearLayout: LinearLayoutManager
    private lateinit var mbinding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        mbinding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(mbinding.root)
        ViewCompat.setOnApplyWindowInsetsListener(mbinding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }
        setupRecyclerView()

        mbinding.fabAddNote.setOnClickListener {
            startActivity(Intent(this, ActionsActivity::class.java))
        }
    }

    private fun setupRecyclerView() {
        noteAdapter = NoteAdapter(mutableListOf(), this)
        mLinearLayout = LinearLayoutManager(this)

        getNotes()
        mbinding.recyclerView.apply {
            setHasFixedSize(true)
            layoutManager = mLinearLayout
            adapter = noteAdapter
        }
        setupSwipe()
    }

    private fun getNotes() {
        Thread {
            val notes =
                NoteApplication.database.noteDao().getAllNotes()
            runOnUiThread {
                noteAdapter.setNotes(notes)
            }
        }.start()
    }

    private fun setupSwipe() {
        val swipeCallback = object : ItemTouchHelper.SimpleCallback(0, ItemTouchHelper.LEFT) {

            override fun onMove(
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                target: RecyclerView.ViewHolder
            ): Boolean {
                return false
            }

            override fun onSwiped(viewHolder: RecyclerView.ViewHolder, direction: Int) {
                val position = viewHolder.adapterPosition
                val note = noteAdapter.getNoteAt(position)
                when (direction) {
                    ItemTouchHelper.LEFT -> {
                        deleteNote(note)
                    }
                }
            }

            override fun onChildDraw(
                c: Canvas,
                recyclerView: RecyclerView,
                viewHolder: RecyclerView.ViewHolder,
                dX: Float,
                dY: Float,
                actionState: Int,
                isCurrentlyActive: Boolean
            ) {
                super.onChildDraw(c, recyclerView, viewHolder, dX, dY, actionState, isCurrentlyActive)
            }
        }

        val itemTouchHelper = ItemTouchHelper(swipeCallback)
        itemTouchHelper.attachToRecyclerView(mbinding.recyclerView)
    }

    private fun deleteNote(note: NoteEntity) {
        Thread {
            NoteApplication.database.noteDao().deleteNote(note)
            runOnUiThread {
                noteAdapter.removeNote(note)
            }
        }.start()
    }



    override fun onClick(note: NoteEntity,position: Int) {
        TODO("Not yet implemented")
    }

    override fun onLongClick(note: NoteEntity) {
        note.isPinned = !note.isPinned
        Thread {
            NoteApplication.database.noteDao().updateNote(note)
            runOnUiThread {
                noteAdapter.update(note)
            }
        }.start()
    }
}