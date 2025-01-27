package com.davpicroc.notepad.mainModule

import android.content.Context
import android.graphics.Canvas
import android.os.Build
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import androidx.recyclerview.widget.ItemTouchHelper
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.davpicroc.notepad.editNoteModule.EditNoteFragment
import com.davpicroc.notepad.NoteApplication
import com.davpicroc.notepad.R
import com.davpicroc.notepad.databinding.ActivityMainBinding
import com.davpicroc.notepad.common.entities.NoteEntity
import com.davpicroc.notepad.common.utils.MainAux
import com.davpicroc.notepad.editNoteModule.viewModel.EditNoteViewModel
import com.davpicroc.notepad.mainModule.adapter.NoteAdapter
import com.davpicroc.notepad.mainModule.adapter.OnClickListener
import com.davpicroc.notepad.mainModule.viewModel.MainViewModel
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlin.properties.Delegates

class MainActivity : AppCompatActivity(),OnClickListener, MainAux {

    private lateinit var noteAdapter: NoteAdapter
    private lateinit var mLinearLayout: LinearLayoutManager
    private lateinit var mbinding: ActivityMainBinding

    private lateinit var mMainViewModel: MainViewModel
    private lateinit var mEditNoteViewModel: EditNoteViewModel

    private val preferences by lazy { getSharedPreferences("MyPreferences", Context.MODE_PRIVATE) }
    private val lastUserIdKey by lazy { getString(R.string.sp_last_user) }
    private var userId by Delegates.notNull<Long>()

    private var isFragmentTransactionInProgress = false

    @RequiresApi(Build.VERSION_CODES.O)
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

        setupViewModel()
        setupRecyclerView()

        mbinding.fabAddNote.setOnClickListener {
            launchEditFragment()
        }
    }

    // Pregunta a Antonio: se puede pasar al constructor del viewmodel el id de usuario para extraer
    // las notas de un usuario en especifico?

    private fun setupViewModel() {
        mMainViewModel = ViewModelProvider(this,MainViewModelFactory(userId))[MainViewModel::class.java]
        mMainViewModel.getNotes().observe(this){ notes ->
            noteAdapter.setNotes(notes)
        }
        mEditNoteViewModel = ViewModelProvider(this).get(EditNoteViewModel::class.java)

        mEditNoteViewModel.getShowFab().observe(this){ isVisible ->
            if (isVisible) mbinding.fabAddNote.show() else mbinding.fabAddNote.hide()
        }

        mEditNoteViewModel.getNoteSelected().observe(this){ noteEntity ->
            if (noteEntity.id != 0L) {
                noteAdapter.add(noteEntity)
            }
        }
    }

    // Preguntar a Antonio: Esto no se ni lo que hace, me lo ha hecho chatgpt
    class MainViewModelFactory(private val userId: Long) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(MainViewModel::class.java)) {
                return MainViewModel(userId) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }


    @RequiresApi(Build.VERSION_CODES.O)
    private fun launchEditFragment(noteEntity: NoteEntity = NoteEntity()) {
        mEditNoteViewModel.setShowFab(false)
        mEditNoteViewModel.setNoteSelected(noteEntity)


        val fragment = EditNoteFragment()
        val fragmentManager = supportFragmentManager

        if (!fragmentManager.isStateSaved) {
            val fragmentTransaction = fragmentManager.beginTransaction()
            fragmentTransaction.add(R.id.containerMain, fragment)
            fragmentTransaction.addToBackStack(null)
            fragmentTransaction.commitAllowingStateLoss()
        }
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
        CoroutineScope(Dispatchers.Main).launch {
            val notes =
                NoteApplication.database.noteDao().getAllNotesFromUser(userId)
            noteAdapter.setNotes(notes)
        }
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



    @RequiresApi(Build.VERSION_CODES.O)
    override fun onClick(note: NoteEntity) {
        launchEditFragment(note)
    }

    override fun onLongClick(note: NoteEntity) {
        note.isPinned = !note.isPinned
        mEditNoteViewModel.updateNote(note)
    }
}