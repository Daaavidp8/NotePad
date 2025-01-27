package com.davpicroc.notepad.common.database

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.Query
import androidx.room.Update
import com.davpicroc.notepad.common.entities.UserEntity

@Dao
interface UserDao {
    @Query("SELECT * FROM UserEntity")
    suspend fun getAllUsers(): MutableList<UserEntity>
    @Query("SELECT * FROM UserEntity WHERE username = :username")
    suspend fun getUserByName(username: String): UserEntity
    @Query("SELECT * FROM UserEntity WHERE id = :id")
    suspend fun getUserById(id: Long): UserEntity
    @Insert
    suspend fun addUser(userEntity: UserEntity): Long
    @Update
    fun updateUser(userEntity: UserEntity)
    @Delete
    fun deleteUser(userEntity: UserEntity)
}