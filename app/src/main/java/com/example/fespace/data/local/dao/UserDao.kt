package com.example.fespace.data.local.dao

import androidx.room.*
import com.example.fespace.data.local.entity.UserEntity
import kotlinx.coroutines.flow.Flow

@Dao
interface UserDao {
        @Query("SELECT * FROM users WHERE email = :email AND password = :pass LIMIT 1")
        suspend fun getUser(email: String, pass: String): UserEntity?    @Insert(onConflict = OnConflictStrategy.REPLACE)
        suspend fun insertUser(user: UserEntity)

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insert(user: UserEntity)

    @Update
    suspend fun updateUser(user: UserEntity)

    @Delete
    suspend fun delete(user: UserEntity)

    @Query("SELECT * FROM users WHERE email = :email AND password = :password LIMIT 1")
    suspend fun login(email: String, password: String): UserEntity?

    @Query("SELECT * FROM users WHERE idUser = :id LIMIT 1")
    suspend fun getUserById(id: Int): UserEntity?

    @Query("SELECT COUNT(*) FROM users WHERE role = 'client'")
    fun getClientCount(): Flow<Int>
}
