package com.davpicroc.notepad.dao

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.davpicroc.notepad.entity.UserEntity

@Dao
interface UserDao {
    @Query("SELECT * FROM UserEntity")
    fun getAllUsers(): MutableList<UserEntity>
    @Query("SELECT * FROM UserEntity WHERE username = :username")
    fun getUserByName(username: String): UserEntity
    @Query("SELECT * FROM UserEntity WHERE id = :id")
    fun getUserById(id: Long): UserEntity
    @Insert
    fun addUser(userEntity: UserEntity)
    @Update
    fun updateUser(userEntity: UserEntity)
    @Delete
    fun deleteUser(userEntity: UserEntity)
}