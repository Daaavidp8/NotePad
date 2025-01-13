package com.davpicroc.Notes.mainModule

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.Menu
import android.view.MenuInflater
import android.view.MenuItem
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.view.MenuProvider
import androidx.core.widget.addTextChangedListener
import com.bumptech.glide.Glide
import com.bumptech.glide.load.engine.DiskCacheStrategy
import com.davpicroc.notepad.databinding.FragmentEditNoteBinding
import com.davpicroc.notepad.mainModule.MainActivity
import com.davpicroc.notepad.R
import com.davpicroc.notepad.NoteApplication
import com.davpicroc.notepad.entity.NoteEntity
import com.google.android.material.dialog.MaterialAlertDialogBuilder
import com.google.android.material.snackbar.Snackbar
import com.google.android.material.textfield.TextInputLayout
import kotlin.properties.Delegates

class EditNoteFragment : Fragment() {

    private lateinit var binding: FragmentEditNoteBinding
    private var menuProvider: MenuProvider? = null
    private var mActivity: MainActivity? = null
    private var isEditMode: Boolean = false
    private var mNoteEntity: NoteEntity? = null


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        binding = FragmentEditNoteBinding.inflate(inflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val activity = activity as? MainActivity
        mActivity = activity
        setupActionBar()
        createMenu()
        val id = arguments?.getLong(getString(R.string.arg_id), 0)
        if (id != null && id != 0L) {
            isEditMode = true
            getNote(id)
        } else {
            isEditMode = false
            mNoteEntity = NoteEntity(Title = "", Content = "", Date = "", isPinned = false, userId = "")
        }
        setupTextFields()
    }

    private fun getNote(id: Long) {
        Thread {
            mNoteEntity = NoteApplication.database.noteDao().getNote(id)
            requireActivity().runOnUiThread {
                if (mNoteEntity != null) {
                    setUINote(mNoteEntity!!)
                }
            }
        }.start()
    }

    private fun setUINote(noteEntity: NoteEntity) {
        with(binding) {
            editTextTitle.setText(noteEntity.Title)
            contentNote.setText(noteEntity.Content)
        }
    }

    private fun createMenu() {
        menuProvider = object : MenuProvider {
            override fun onCreateMenu(menu: Menu, menuInflater: MenuInflater) {
                menuInflater.inflate(R.menu.menu, menu)
            }

            override fun onMenuItemSelected(menuItem: MenuItem): Boolean {
                return when (menuItem.itemId) {
                    android.R.id.home -> {
                        mActivity?.onBackPressedDispatcher?.onBackPressed()
                        true
                    }

                    R.id.action_save -> {
                        if (mNoteEntity != null &&
                            validateFields(binding., binding.contentNote)) {
                            with(mNoteEntity!!) {
                                name = binding.etName.text.toString().trim()
                                phone = binding.etPhone.text.toString().trim()
                                website = binding.etWebsite.text.toString().trim()
                                photoUrl = binding.etPhotoUrl.text.toString().trim()
                            }

                            Thread {
                                if (isEditMode) {
                                    NoteApplication.database.NoteDao().updateNote(mNoteEntity!!)
                                } else {
                                    mNoteEntity!!.id =
                                        NoteApplication.database.NoteDao().addNote(mNoteEntity!!)
                                }
                                requireActivity().runOnUiThread {
                                    hideKeyboard()
                                    if (isEditMode) {
                                        mActivity?.updateNote(mNoteEntity!!)
                                        Snackbar.make(
                                            binding.root,
                                            R.string.edit_Note_message_update_success,
                                            Snackbar.LENGTH_SHORT
                                        ).show()
                                        mActivity?.onBackPressedDispatcher?.onBackPressed()
                                    } else {
                                        mActivity?.addNote(mNoteEntity!!)
                                        Toast.makeText(
                                            mActivity,
                                            R.string.edit_Note_message_save_success,
                                            Toast.LENGTH_SHORT
                                        ).show()
                                        mActivity?.onBackPressedDispatcher?.onBackPressed()
                                    }
                                }
                            }.start()
                        }
                        true
                    }

                    else -> false
                }
            }
        }
        menuProvider?.let {
            requireActivity().addMenuProvider(it)
        }
    }

    private fun setupActionBar() {
        mActivity = activity as? MainActivity
        mActivity?.supportActionBar?.setDisplayHomeAsUpEnabled(true)
        if (isEditMode) {
            mActivity?.supportActionBar?.title =
                getString(R.string.edit_Note_title_edit)
        } else {
            mActivity?.supportActionBar?.title =
                getString(R.string.edit_Note_title_add)
        }
    }

    private fun setupTextFields() {
        with(binding) {
            etName.addTextWatcher {
                validateFields(tilName)
            }
            etPhone.addTextWatcher {
                validateFields(tilPhone)
            }
            etPhotoUrl.addTextWatcher {
                validateFields(tilPhotoUrl)
                loadImage(it.toString().trim())
            }
        }
    }

    private fun EditText.addTextWatcher(afterTextChanged:
                                            (Editable?) -> Unit) {
        this.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) =
                afterTextChanged(s)
            override fun beforeTextChanged(s: CharSequence?,
                                           start: Int, count: Int, after: Int) { /* TODO */ }
            override fun onTextChanged(s: CharSequence?, start:
            Int, before: Int, count: Int) { /* TODO */ }
        })
    }
    private fun loadImage(url: String) {
        Glide.with(this)
            .load(url)
            .diskCacheStrategy(DiskCacheStrategy.ALL)
            .centerCrop()
            .into(binding.imgPhoto)
    }

    private fun validateFields(vararg textFields:
                               TextInputLayout
    ): Boolean {
        var isValid = true
        for (textField in textFields) {
            if (textField.editText?.text.toString().trim().isEmpty()) {
                textField.error = getString(R.string.helper_required)
                textField.editText?.requestFocus()
                isValid = false
            } else {
                textField.error = null
            }
        }
        if (!isValid) {
            Snackbar.make(
                binding.root,
                R.string.edit_Note_message_valid,
                Snackbar.LENGTH_SHORT
            ).show()
        }
        return isValid
    }

    private fun hideKeyboard() {
        val imm =
            mActivity?.getSystemService(Context.INPUT_METHOD_SERVICE) as InputMethodManager
        if (view != null) {
            imm.hideSoftInputFromWindow(requireView().windowToken, 0)
        }
    }

    override fun onDestroy() {
        mActivity?.supportActionBar?.setDisplayHomeAsUpEnabled(false)
        mActivity?.supportActionBar?.title = getString(R.string.app_name)
        mActivity?.hideFab(true)
        menuProvider?.let {
            requireActivity().removeMenuProvider(it)
        }
        mActivity?.hideFab(true)
        super.onDestroy()
    }

    override fun onDestroyView() {
        hideKeyboard()
        super.onDestroyView()
    }



}