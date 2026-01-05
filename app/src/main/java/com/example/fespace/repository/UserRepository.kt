package com.example.fespace.repository

import com.example.fespace.data.local.dao.UserDao
import com.example.fespace.data.local.entity.UserEntity
import kotlinx.coroutines.flow.Flow

class UserRepository(private val userDao: UserDao) {

    suspend fun getUser(email: String, pass: String): UserEntity? {
        return userDao.getUser(email, pass)
    }

    suspend fun insertUser(user: UserEntity) {
        userDao.insertUser(user)
    }

    suspend fun login(email: String, password: String): UserEntity? {
        return userDao.login(email, password)
    }

    suspend fun getUserById(userId: Int): UserEntity? {
        return userDao.getUserById(userId)
    }

    suspend fun update(user: UserEntity) {
        userDao.updateUser(user) // Pastikan nama di Dao sesuai
    }

    suspend fun deleteUser(user: UserEntity) {
        userDao.delete(user)
    }

    fun getClientCount(): Flow<Int> = userDao.getClientCount()
}
