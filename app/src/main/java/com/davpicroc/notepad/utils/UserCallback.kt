package com.davpicroc.notepad.utils

import com.davpicroc.notepad.entity.UserEntity

interface UserCallback {
    fun onUserFound(user: UserEntity?)
}