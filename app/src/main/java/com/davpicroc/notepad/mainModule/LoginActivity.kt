package com.davpicroc.notepad.mainModule

import android.content.Context
import android.content.Intent
import android.os.Build
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.annotation.RequiresApi
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import androidx.lifecycle.ViewModelProvider
import androidx.lifecycle.lifecycleScope
import com.bumptech.glide.Glide
import com.davpicroc.notepad.NoteApplication
import com.davpicroc.notepad.R
import com.davpicroc.notepad.databinding.ActivityLoginBinding
import com.davpicroc.notepad.common.entities.UserEntity
import com.davpicroc.notepad.editNoteModule.viewModel.EditNoteViewModel
import com.davpicroc.notepad.editNoteModule.viewModel.EditUserViewModel
import com.davpicroc.notepad.mainModule.MainActivity.MainViewModelFactory
import com.davpicroc.notepad.mainModule.viewModel.LoginViewModel
import com.davpicroc.notepad.mainModule.viewModel.MainViewModel
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.json.JSONObject

class LoginActivity : AppCompatActivity() {

    private lateinit var lbinding: ActivityLoginBinding
    private lateinit var mEditUserViewModel: EditUserViewModel
    private lateinit var mLoginViewModel: LoginViewModel

    private val preferences by lazy { getSharedPreferences("MyPreferences", Context.MODE_PRIVATE) }
    private val lastUserIdKey by lazy { getString(R.string.sp_last_user) }
    private val usersJsonKey by lazy { getString(R.string.users) }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        lbinding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(lbinding.root)

        ViewCompat.setOnApplyWindowInsetsListener(lbinding.root) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }


        mLoginViewModel = ViewModelProvider(this)[LoginViewModel::class.java]

        val usernameEditText = lbinding.tietUsername
        val passwordEditText = lbinding.tietPassword

        loadLastUser()

        Glide.with(this)
            .load("https://i.pinimg.com/736x/a1/29/03/a12903f9b0eee457230cf692bec3a964.jpg")
            .centerCrop()
            .into(lbinding.imgBackground)

        lbinding.btnLogin.setOnClickListener {
            val usernameInput = usernameEditText.text.toString()
            val passwordInput = passwordEditText.text.toString()

            runBlocking {
                if (validateLogin(usernameInput, passwordInput)) {
                    saveLastUserId(usernameInput)
                    navigateToMainActivity()
                } else {
                    showSnackbar("Credenciales inválidas.")
                }
            }
        }

        lbinding.btnRegister.setOnClickListener {
            val usernameInput = usernameEditText.text.toString()
            val passwordInput = passwordEditText.text.toString()

            runBlocking {
                val message = handleRegistration(usernameInput, passwordInput)
                showSnackbar(message)
            }
        }
    }


    private suspend fun validateLogin(username: String, password: String): Boolean {
        val user = getUserByUsername(username)
        return user != null && user.password == password
    }

    private suspend fun getUserByUsername(username: String): UserEntity? {
        return try {
            mLoginViewModel.getUserByName(username)
        } catch (e: Exception) {
            null
        }
    }

    private suspend fun handleRegistration(username: String, password: String): String {
        return when {
            username.isEmpty() || password.isEmpty() -> "Las credenciales no pueden estar vacías."
            isUserRegistered(username) -> "Este usuario ya está registrado."
            else -> {
                registerUser(username, password)
                "Usuario registrado correctamente!"
            }
        }
    }

    private suspend fun isUserRegistered(username: String): Boolean {
        val user = getUserByUsername(username)
        return user != null
    }

    private fun registerUser(username: String, password: String) {
        val newUser = UserEntity(username = username, password = password)
        mEditUserViewModel.saveUser(newUser)

        val usersJson = JSONObject(mapOf(username to password)).toString()
        preferences.edit().putString(usersJsonKey, usersJson).apply()
    }

    private suspend fun saveLastUserId(username: String) {
        val user = getUserByUsername(username)
        val lastUserId = user?.id ?: 0
        preferences.edit().putLong(lastUserIdKey, lastUserId).apply()
    }

    private fun loadLastUser() {
        val lastUserId = preferences.getLong(lastUserIdKey, 0)
        if (lastUserId != 0L) {
            lbinding.rememberCheckbox.isChecked = true
            lifecycleScope.launch {
                val lastUser = getUserById(lastUserId)

                if (lastUser != null) {
                    lbinding.tietUsername.setText(lastUser.username)
                    lbinding.tietPassword.setText(lastUser.password)
                } else {
                    showSnackbar("Usuario no encontrado.")
                }
            }
        }
    }

    private suspend fun getUserById(id: Long): UserEntity? {
        return try {
            mLoginViewModel.getUserById(id)
        } catch (e: Exception) {
            null
        }
    }

    private fun navigateToMainActivity() {
        startActivity(Intent(this, MainActivity::class.java))
        finish()
    }

    private fun showSnackbar(message: String) {
        Snackbar.make(lbinding.root, message, Snackbar.LENGTH_LONG).show()
    }
}
