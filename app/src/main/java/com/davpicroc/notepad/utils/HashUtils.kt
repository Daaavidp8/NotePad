package com.davpicroc.notepad.utils

import android.os.Build
import androidx.annotation.RequiresApi
import javax.crypto.Cipher
import javax.crypto.KeyGenerator
import javax.crypto.SecretKey
import javax.crypto.spec.SecretKeySpec
import java.util.Base64

object HashUtils {

    @RequiresApi(Build.VERSION_CODES.O)
    fun encrypt(text: String): String {
        val cipher = Cipher.getInstance("AES")
        val encryptedBytes = cipher.doFinal(text.toByteArray())
        return Base64.getEncoder().encodeToString(encryptedBytes)
    }

    @RequiresApi(Build.VERSION_CODES.O)
    fun decrypt(encryptedText: String): String {
        val cipher = Cipher.getInstance("AES")
        val decodedBytes = Base64.getDecoder().decode(encryptedText)
        val originalBytes = cipher.doFinal(decodedBytes)
        return String(originalBytes)
    }
}
