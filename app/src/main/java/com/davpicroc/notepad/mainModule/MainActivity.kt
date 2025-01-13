package com.davpicroc.notepad.mainModule

import android.content.Context
import android.graphics.Canvas
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.davpicroc.Notes.mainModule.EditNoteFragment
import com.davpicroc.notepad.NoteApplication
import com.davpicroc.notepad.R
import com.davpicroc.notepad.databinding.ActivityMainBinding
import com.davpicroc.notepad.entity.NoteEntity
import com.davpicroc.notepad.mainModule.adapter.NoteAdapter
import com.davpicroc.notepad.mainModule.adapter.OnClickListener
import kotlin.properties.Delegates

class MainActivity : AppCompatActivity(),OnClickListener, MainAux {

    private lateinit var noteAdapter: NoteAdapter
    private lateinit var mLinearLayout: LinearLayoutManager
    private lateinit var mbinding: ActivityMainBinding

    private val preferences by lazy { getSharedPreferences("MyPreferences", Context.MODE_PRIVATE) }
    private val lastUserIdKey by lazy { getString(R.string.sp_last_user) }
    private var userId by Delegates.notNull<Long>()

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
        loadUserId()


        setupRecyclerView()

        mbinding.fabAddNote.setOnClickListener {
            launchEditFragment()
        }
    }

    private fun launchEditFragment(args: Bundle? = null) {
        val fragment = EditNoteFragment()
        if (args != null) fragment.arguments = args
        val fragmentManager = supportFragmentManager
        val fragmentTransaction =
            fragmentManager.beginTransaction()
        fragmentTransaction.add(R.id.main, fragment)
        fragmentTransaction.addToBackStack(null)
        fragmentTransaction.commit()
        hideFab()
    }


    private fun loadUserId() {
       userId = preferences.getLong(lastUserIdKey, 0)
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
                NoteApplication.database.noteDao().getAllNotesFromUser(userId)
            runOnUiThread {
                noteAdapter.setNotes(notes)
            }
        }.start()
    }

    override fun hideFab(isVisible: Boolean) {
        if (isVisible) mbinding.fabAddNote.show() else mbinding.fabAddNote.hide()
    }

    override fun addNote(noteEntity: NoteEntity) {
        noteAdapter.add(noteEntity)
    }

    override fun updateNote(noteEntity: NoteEntity) {
        noteAdapter.update(noteEntity)
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



    override fun onClick(note: NoteEntity) {
        val args = Bundle()
        args.putLong(getString(R.string.arg_id), note.id)
        launchEditFragment(args)
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