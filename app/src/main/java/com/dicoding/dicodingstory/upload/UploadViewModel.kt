package com.dicoding.dicodingstory.upload

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.dicoding.dicodingstory.data.response.UploadResponse
import com.dicoding.dicodingstory.data.retrofit.ApiConfig
import com.google.gson.Gson
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.HttpException
import java.io.File

class UploadViewModel : ViewModel() {

    fun uploadImage(
        imageFile: File,
        description: String,
        token: String?,
        onSuccess: (UploadResponse) -> Unit,
        onError: (String) -> Unit
    ) {
        viewModelScope.launch {
            try {
                val requestBody = description.toRequestBody("text/plain".toMediaType())
                val requestImageFile = imageFile.asRequestBody("image/jpeg".toMediaType())
                val multipartBody = MultipartBody.Part.createFormData(
                    "photo",
                    imageFile.name,
                    requestImageFile
                )

                val apiService = ApiConfig.apiInstant
                val successResponse = apiService.AddStory(multipartBody, requestBody, "Bearer $token")

                withContext(Dispatchers.Main) {
                    onSuccess(successResponse)
                }
            } catch (e: HttpException) {
                val errorBody = e.response()?.errorBody()?.string()
                val errorResponse = Gson().fromJson(errorBody, UploadResponse::class.java)

                withContext(Dispatchers.Main) {
                    onError(errorResponse.message)
                }
            }
        }
    }

}