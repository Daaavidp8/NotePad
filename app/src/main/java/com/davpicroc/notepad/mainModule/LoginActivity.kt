package com.davpicroc.notepad.mainModule

import android.os.Bundle
import androidx.activity.enableEdgeToEdge
import androidx.appcompat.app.AppCompatActivity
import androidx.core.view.ViewCompat
import androidx.core.view.WindowInsetsCompat
import com.bumptech.glide.Glide
import com.davpicroc.notepad.R
import com.davpicroc.notepad.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {

    lateinit var lbinding: ActivityLoginBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        enableEdgeToEdge()
        lbinding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(lbinding.root)
        ViewCompat.setOnApplyWindowInsetsListener(findViewById(R.id.main)) { v, insets ->
            val systemBars = insets.getInsets(WindowInsetsCompat.Type.systemBars())
            v.setPadding(systemBars.left, systemBars.top, systemBars.right, systemBars.bottom)
            insets
        }

        Glide.with(this)
            .load("https://i.pinimg.com/736x/a1/29/03/a12903f9b0eee457230cf692bec3a964.jpg")
            .centerCrop()
            .into(lbinding.imgBackground)
    }
}