package com.dicoding.dicodingstory.data.injection

import android.content.Context
import com.dicoding.dicodingstory.data.database.StoryDatabase
import com.dicoding.dicodingstory.data.pref.UserPreferences
import com.dicoding.dicodingstory.data.pref.dataStore
import com.dicoding.dicodingstory.data.repository.StoryRepository
import com.dicoding.dicodingstory.data.repository.UserRepository
import com.dicoding.dicodingstory.data.retrofit.ApiConfig
import com.dicoding.dicodingstory.data.retrofit.ApiService

object Injection {
    fun provideStoryRepository(context: Context, userPreference: UserPreferences): StoryRepository {
        val database = StoryDatabase.getDatabase(context)
        val apiService = ApiConfig.apiInstant
        return StoryRepository(database, apiService, userPreference)
    }

    fun provideRepository(context: Context): UserRepository {
        val pref = UserPreferences.getInstance(context.dataStore)
        return UserRepository.getInstance(pref)
    }

    fun provideApiService(): ApiService {
        return ApiConfig.apiInstant
    }
}