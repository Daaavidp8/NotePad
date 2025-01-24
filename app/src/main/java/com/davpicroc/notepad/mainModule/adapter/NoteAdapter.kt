package com.davpicroc.notepad.mainModule.adapter

import android.annotation.SuppressLint
import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.davpicroc.notepad.R
import com.davpicroc.notepad.databinding.ItemNoteBinding
import com.davpicroc.notepad.common.entities.NoteEntity
import java.io.Serializable

class NoteAdapter(private var notes: MutableList<NoteEntity>,
                  private var listener: OnClickListener):
    RecyclerView.Adapter<NoteAdapter.ViewHolder>(),Serializable{

    private lateinit var mContext: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteAdapter.ViewHolder {
        mContext = parent.context
        val view =
            LayoutInflater.from(mContext).inflate(
                R.layout.item_note,
                parent, false)
        return ViewHolder(view)
    }

    fun getNoteAt(position: Int): NoteEntity {
        return notes[position]
    }

    fun removeNote(note: NoteEntity) {
        val position = notes.indexOf(note)
        if (position != -1) {
            notes.removeAt(position)
            notifyItemRemoved(position)
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun add(note: NoteEntity) {
        notes.add(note)
        notifyDataSetChanged()
    }

    fun update(note: NoteEntity) {
        val index = notes.indexOfFirst { it.id == note.id }
        if (index != -1) {
            notes[index] = note
            notifyItemChanged(index)
        }
    }

    @SuppressLint("NotifyDataSetChanged")
    fun setNotes(notes: List<NoteEntity>) {
        this.notes = notes as MutableList<NoteEntity>
        notifyDataSetChanged()
    }

    override fun onBindViewHolder(holder: NoteAdapter.ViewHolder, position: Int) {
        val note = notes[position]
        with(holder) {
            setListener(note)
            binding.tvTitle.text = note.Title
            binding.tvDate.text = note.Date
            binding.tvDescription.text = note.Content
            binding.imagePinned.isChecked = note.isPinned
        }
    }

    override fun getItemCount(): Int = notes.size

    inner class ViewHolder(view: View):
        RecyclerView.ViewHolder(view) {
        val binding = ItemNoteBinding.bind(view)
        fun setListener(note: NoteEntity) {
            binding.root.setOnClickListener{
                listener.onClick(note)
            }
            binding.root.setOnLongClickListener{
                listener.onLongClick(note)
                true
            }
        }
    }
}