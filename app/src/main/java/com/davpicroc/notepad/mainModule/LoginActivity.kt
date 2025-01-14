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
import com.bumptech.glide.Glide
import com.davpicroc.notepad.NoteApplication
import com.davpicroc.notepad.R
import com.davpicroc.notepad.databinding.ActivityLoginBinding
import com.davpicroc.notepad.entity.UserEntity
import com.davpicroc.notepad.utils.HashUtils
import com.google.android.material.snackbar.Snackbar
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.async
import kotlinx.coroutines.runBlocking
import kotlinx.coroutines.withContext
import org.json.JSONObject
import javax.crypto.SecretKey

class LoginActivity : AppCompatActivity() {

    private lateinit var lbinding: ActivityLoginBinding

    private val preferences by lazy { getSharedPreferences("MyPreferences", Context.MODE_PRIVATE) }
    private val lastUserIdKey by lazy { getString(R.string.sp_last_user) }
    private val usersJsonKey by lazy { getString(R.string.users) }

    @RequiresApi(Build.VERSION_CODES.O)
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

            if (validateLogin(usernameInput, passwordInput)) {
                saveLastUserId(usernameInput)
                navigateToMainActivity()
            } else {
                showSnackbar("Credenciales inválidas.")
            }
        }

        lbinding.btnRegister.setOnClickListener {
            val usernameInput = usernameEditText.text.toString()
            val passwordInput = passwordEditText.text.toString()

            val message = handleRegistration(usernameInput, passwordInput)
            showSnackbar(message)
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun validateLogin(username: String, password: String): Boolean {
        val user = runBlocking {
            val user = async { getUserByUsername(username) }
            user.await()
        }
        return if (user != null) {
            val hashedPassword = HashUtils.encrypt(password)
            user.password == hashedPassword
        } else {
            false
        }
    }

    private suspend fun getUserByUsername(username: String): UserEntity? {
        return try {
            withContext(Dispatchers.IO) {
                NoteApplication.database.userDao().getUserByName(username)
            }
        } catch (e: Exception) {
            null
        }
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun handleRegistration(username: String, password: String): String {
        return when {
            username.isEmpty() || password.isEmpty() -> "Las credenciales no pueden estar vacías."
            isUserRegistered(username) -> "Este usuario ya está registrado."
            else -> {
                registerUser(username, password)
                "Usuario registrado correctamente!"
            }
        }
    }

    private fun isUserRegistered(username: String): Boolean {
        val user = runBlocking {
            val user = async { getUserByUsername(username) }
            user.await()
        }
        val resultado = user != null
        return resultado
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun registerUser(username: String, password: String) {
        val hashedPassword = HashUtils.encrypt(password)
        val newUser = UserEntity(username = username, password = hashedPassword)


        Thread{
            NoteApplication.database.userDao().addUser(newUser)
        }.start()

        val usersJson = JSONObject(mapOf(username to hashedPassword)).toString()
        preferences.edit().putString(usersJsonKey, usersJson).apply()
    }

    private fun saveLastUserId(username: String) {
        val user = runBlocking {
            val user = async { getUserByUsername(username) }
            user.await()
        }
        val lastUserId = user?.id ?: 0
        preferences.edit().putLong(lastUserIdKey, lastUserId).apply()
    }

    @RequiresApi(Build.VERSION_CODES.O)
    private fun loadLastUser() {
        val lastUserId = preferences.getLong(lastUserIdKey, 0)
        if (lastUserId != 0L) {
            lbinding.rememberCheckbox.isChecked = true
            val lastUser = runBlocking {
                val user = async { getUserById(lastUserId) }
                user.await()
            }
            lbinding.tietUsername.setText(lastUser?.username)
            lbinding.tietPassword.setText(HashUtils.decrypt(lastUser?.password.toString()))
        }
    }

    private suspend fun getUserById(id: Long): UserEntity? {
        return try {
            withContext(Dispatchers.IO) {
                NoteApplication.database.userDao().getUserById(id)
            }
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
