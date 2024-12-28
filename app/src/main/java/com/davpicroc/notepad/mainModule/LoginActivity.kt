package com.davpicroc.notepad.mainModule

import android.content.Context
import android.content.Intent
import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.davpicroc.notepad.R
import com.davpicroc.notepad.databinding.ActivityLoginBinding
import com.google.android.material.snackbar.Snackbar
import org.json.JSONObject

class LoginActivity : AppCompatActivity() {

    private lateinit var lbinding: ActivityLoginBinding

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

        val preferences = getPreferences(Context.MODE_PRIVATE)
        val lastUser = preferences.getString(getString(R.string.sp_last_user),
            null)

        val usernameEditText = lbinding.tietUsername
        val passwordEditText = lbinding.tietPassword


        if (lastUser != null){
            lbinding.rememberCheckbox.isChecked = true
            val credentials = lastUser.removeSurrounding("{", "}").split("=")
            passwordEditText.setText(credentials[0])
            usernameEditText.setText(credentials[1])
        }

        Glide.with(this)
            .load("https://i.pinimg.com/736x/a1/29/03/a12903f9b0eee457230cf692bec3a964.jpg")
            .centerCrop()
            .into(lbinding.imgBackground)

        val users = mutableMapOf<String, String>()

        val sharedPreferences = preferences.getString(getString(R.string.users), "{}")
        val savedUsers = JSONObject(sharedPreferences ?: "{}")
        savedUsers.keys().forEach {
            users[it] = savedUsers.getString(it)
        }

        lbinding.btnLogin.setOnClickListener {

            val usernameInput = usernameEditText.text.toString()
            val passwordInput = passwordEditText.text.toString()

            if (users[usernameInput] == passwordInput) {
                if (lbinding.rememberCheckbox.isChecked){
                    preferences.edit().putString(getString(R.string.sp_last_user), mapOf(usernameInput to passwordInput).toString()).apply()
                }else{
                    preferences.edit().putString(getString(R.string.sp_last_user), null).apply()
                }
                startActivity(Intent(this, MainActivity::class.java))
            } else {
                Snackbar.make(
                    findViewById(android.R.id.content),
                    "Credenciales inválidas.",
                    Snackbar.LENGTH_LONG
                ).show()
            }
        }

        lbinding.btnRegister.setOnClickListener {
            val usernameInput = lbinding.tietUsername.text.toString()
            val passwordInput = lbinding.tietPassword.text.toString()
            var message: String

            if (usernameInput.isNotEmpty() && passwordInput.isNotEmpty()) {
                if (!users.containsKey(usernameInput)) {
                    users[usernameInput] = passwordInput

                    with(preferences.edit()) {
                        val usersJson = JSONObject(users as Map<*, *>).toString()
                        putString(getString(R.string.users), usersJson).apply()
                    }

                    message = "Usuario registrado correctamente!"
                } else {
                    message = "Este usuario ya está registrado."
                }
            } else {
                message = "Las credenciales no pueden estar vacías."
            }

            Snackbar.make(findViewById(android.R.id.content), message, Snackbar.LENGTH_LONG).show()
        }
    }
}