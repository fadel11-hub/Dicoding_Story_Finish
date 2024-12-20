package com.dicoding.dicodingstory.data.repository

import com.dicoding.dicodingstory.data.pref.UserModel
import com.dicoding.dicodingstory.data.pref.UserPreferences
import kotlinx.coroutines.flow.Flow

class UserRepository private constructor(
    private val userPreferences: UserPreferences
) {

    suspend fun saveSession(user: UserModel) {
        userPreferences.saveSession(user)
    }

    fun getSession(): Flow<UserModel> {
        return userPreferences.getSession()
    }

    suspend fun logout() {
        userPreferences.logout()
    }

    companion object {
        @Volatile
        private var instance: UserRepository? = null
        fun getInstance(
            userPreference: UserPreferences
        ): UserRepository =
            instance ?: synchronized(this) {
                instance ?: UserRepository(userPreference)
            }.also { instance = it }
    }
}