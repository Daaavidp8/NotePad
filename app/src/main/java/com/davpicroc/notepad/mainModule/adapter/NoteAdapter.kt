package com.davpicroc.notepad.mainModule.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.davpicroc.notepad.R
import com.davpicroc.notepad.databinding.ItemNoteBinding
import com.davpicroc.notepad.entity.NoteEntity

class NoteAdapter(private var notes: MutableList<NoteEntity>,
                  private var listener: OnClickListener):
    RecyclerView.Adapter<NoteAdapter.ViewHolder>(){

    private lateinit var mContext: Context

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): NoteAdapter.ViewHolder {
        mContext = parent.context
        val view =
            LayoutInflater.from(mContext).inflate(
                R.layout.item_note,
                parent, false)
        return ViewHolder(view)
    }

    override fun onBindViewHolder(holder: NoteAdapter.ViewHolder, position: Int) {
        val note = notes[position]
        with(holder) {
            setListener(note)
            binding.tvTitle.text = note.Title
        }
    }

    override fun getItemCount(): Int = notes.size

    inner class ViewHolder(view: View):
        RecyclerView.ViewHolder(view) {
        val binding = ItemNoteBinding.bind(view)
        fun setListener(note: NoteEntity) {
            binding.root.setOnClickListener{
                listener.onClick(note) }
        }
    }
}