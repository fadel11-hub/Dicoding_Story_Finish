package com.dicoding.dicodingstory.login

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.dicodingstory.data.pref.UserModel
import com.dicoding.dicodingstory.data.repository.UserRepository
import com.dicoding.dicodingstory.data.response.LoginResponse
import com.dicoding.dicodingstory.data.retrofit.ApiService
import kotlinx.coroutines.launch
import retrofit2.Call

class LoginViewModel(
    private val repository: UserRepository,
    private val apiService: ApiService
) : ViewModel() {

    fun login(email: String, password: String): Call<LoginResponse> {
        return apiService.login(email, password)
    }


    fun saveSession(user: UserModel) {
        viewModelScope.launch {
            repository.saveSession(user)
        }
    }
}