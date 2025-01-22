package com.davpicroc.notepad.utils

import com.davpicroc.notepad.common.entities.UserEntity

interface UserCallback {
    fun onUserFound(user: UserEntity?)
}