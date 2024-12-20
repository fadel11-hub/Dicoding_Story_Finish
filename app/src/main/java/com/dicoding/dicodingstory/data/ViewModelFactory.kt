package com.dicoding.dicodingstory.data

import android.content.Context
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.dicoding.dicodingstory.customview.maps.MapsViewModel
import com.dicoding.dicodingstory.data.injection.Injection
import com.dicoding.dicodingstory.data.pref.UserPreferences
import com.dicoding.dicodingstory.data.repository.StoryRepository
import com.dicoding.dicodingstory.data.repository.UserRepository
import com.dicoding.dicodingstory.data.retrofit.ApiService
import com.dicoding.dicodingstory.login.LoginViewModel
import com.dicoding.dicodingstory.main.MainViewModel
import com.dicoding.dicodingstory.signup.SignupViewModel

class ViewModelFactory(
    private val repository: UserRepository,
    private val apiService: ApiService,
    private val storyRepository: StoryRepository
) : ViewModelProvider.NewInstanceFactory() {

    @Suppress("UNCHECKED_CAST")
    override fun <T : ViewModel> create(modelClass: Class<T>): T {
        return when {
            modelClass.isAssignableFrom(MainViewModel::class.java) -> {
                MainViewModel(storyRepository, repository) as T
            }
            modelClass.isAssignableFrom(LoginViewModel::class.java) -> {
                LoginViewModel(repository, apiService) as T
            }
            modelClass.isAssignableFrom(SignupViewModel::class.java) -> {
                SignupViewModel(apiService) as T
            }
            modelClass.isAssignableFrom(MapsViewModel::class.java) -> {
                MapsViewModel() as T
            }

            else -> throw IllegalArgumentException("Unknown ViewModel class: " + modelClass.name)
        }
    }

    companion object {
        @Volatile
        private var INSTANCE: ViewModelFactory? = null
        @JvmStatic
        fun getInstance(context: Context, userPreference: UserPreferences): ViewModelFactory {
            if (INSTANCE == null) {
                synchronized(ViewModelFactory::class.java) {
                    INSTANCE = ViewModelFactory(
                        Injection.provideRepository(context),
                        Injection.provideApiService(),
                        Injection.provideStoryRepository(context, userPreference)
                    )

                }
            }
            return INSTANCE as ViewModelFactory
        }
    }
}