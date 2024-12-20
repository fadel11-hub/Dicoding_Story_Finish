package com.dicoding.dicodingstory.signup

import androidx.lifecycle.ViewModel
import com.dicoding.dicodingstory.data.response.SignupResponse
import com.dicoding.dicodingstory.data.retrofit.ApiService
import retrofit2.Call

class SignupViewModel (
    private val apiService: ApiService
) : ViewModel() {

    fun register (name: String, email: String, password: String): Call<SignupResponse> {
        return apiService.register(name, email, password)
    }
}