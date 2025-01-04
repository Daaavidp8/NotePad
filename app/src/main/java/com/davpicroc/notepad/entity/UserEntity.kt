package com.davpicroc.notepad.entity

import androidx.room.Entity
import androidx.room.PrimaryKey

@Entity(tableName = "UserEntity")
data class UserEntity(
    @PrimaryKey(autoGenerate = true) val id: Long = 0,
    var username: String,
    var password: String
)

