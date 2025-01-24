package com.davpicroc.notepad.editNoteModule

import android.content.Context
import android.os.Build
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.view.inputmethod.InputMethodManager
import android.widget.EditText
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.core.view.MenuProvider
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.ViewModelProvider
import com.davpicroc.notepad.databinding.FragmentEditNoteBinding
import com.davpicroc.notepad.mainModule.MainActivity
import com.davpicroc.notepad.R
import com.davpicroc.notepad.NoteApplication
import com.davpicroc.notepad.common.entities.NoteEntity
import com.davpicroc.notepad.editNoteModule.viewModel.EditNoteViewModel
import com.google.android.material.snackbar.Snackbar
import java.time.LocalDateTime
import java.time.format.DateTimeFormatter
import kotlin.properties.Delegates

class EditNoteFragment : Fragment() {

    private lateinit var binding: FragmentEditNoteBinding
    private var menuProvider: MenuProvider? = null
    private var mActivity: MainActivity? = null
    private var isEditMode: Boolean = false
    private var mNoteEntity: NoteEntity? = null
    private lateinit var mEditNoteViewModel: EditNoteViewModel

    private val preferences by lazy {
        requireContext().getSharedPreferences("MyPreferences", Context.MODE_PRIVATE)
    }

    private val lastUserIdKey by lazy {
        getString(R.string.sp_last_user)
    }

    private var userIdpref: Long by Delegates.notNull()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        mEditNoteViewModel = ViewModelProvider(requireActivity())[EditNoteViewModel::class.java]
    }


    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        userIdpref = preferences.getLong(lastUserIdKey, -1L)
        if (userIdpref == -1L) {
            throw IllegalStateException("User ID no encontrado")
        }
        binding = FragmentEditNoteBinding.inflate(inflater, container, false)
        return binding.root
    }

    @RequiresApi(Build.VERSION_CODES.O)
    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val activity = activity as? MainActivity
        mActivity = activity


        setupViewModel()

        binding.buttonBack.setOnClickListener{
            mActivity?.onBackPressedDispatcher?.onBackPressed()
        }

        binding.actionSave.setOnClickListener{
            if (mNoteEntity != null &&
                validateFields(binding.editTextTitle, binding.contentNote)) {
                with(mNoteEntity!!) {
                    Title = binding.editTextTitle.text.toString().trim()
                    Content = binding.contentNote.text.toString().trim()
                    Date = LocalDateTime.now().format(DateTimeFormatter.ofPattern("dd/MM/yyyy"))
                    userId = userIdpref
                }

                if (isEditMode){
                    mEditNoteViewModel.updateNote(mNoteEntity!!)
                }else{
                    mEditNoteViewModel.saveNote(mNoteEntity!!)
                }
            }
        }

        setupTextFields()
    }

    private fun setupViewModel() {
        mEditNoteViewModel.getNoteSelected().observe(viewLifecycleOwner){
            mNoteEntity = it
            if (it.id != 0L) {
                isEditMode = true
                setUINote(mNoteEntity!!)
            } else {
                isEditMode = false
            }
        }

        mEditNoteViewModel.getResult().observe(viewLifecycleOwner){ result ->
            hideKeyboard()

            when(result){
                is Long -> {
                    mNoteEntity!!.id = result
                    mEditNoteViewModel.setNoteSelected(mNoteEntity!!)

                    Toast.makeText(
                        mActivity,
                        R.string.edit_Note_message_save_success,
                        Toast.LENGTH_SHORT
                    ).show()

                    mActivity?.onBackPressedDispatcher?.onBackPressed()
                }
                is NoteEntity -> {
                    mEditNoteViewModel.setNoteSelected(mNoteEntity!!)

                    Toast.makeText(
                        mActivity,
                        R.string.edit_Note_message_update_success,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            }
        }
    }

    private fun setUINote(noteEntity: NoteEntity) {
        with(binding) {
            editTextTitle.setText(noteEntity.Title)
            contentNote.setText(noteEntity.Content)
        }
    }

    private fun setupTextFields() {
        with(binding) {
            editTextTitle.addTextWatcher {
                validateFields(editTextTitle)
            }
            contentNote.addTextWatcher {
                validateFields(contentNote)
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

    private fun validateFields(vararg textFields:
                               EditText
    ): Boolean {
        var isValid = true
        for (textField in textFields) {
            if (textField.text.toString().trim().isEmpty()) {
                textField.error = getString(R.string.helper_required)
                textField.requestFocus()
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