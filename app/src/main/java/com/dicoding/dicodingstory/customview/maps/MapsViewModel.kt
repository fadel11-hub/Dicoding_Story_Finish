package com.dicoding.dicodingstory.customview.maps

import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import com.dicoding.dicodingstory.data.response.StoriesResponse
import com.dicoding.dicodingstory.data.response.StoryResponse
import com.dicoding.dicodingstory.data.retrofit.ApiConfig

class MapsViewModel : ViewModel() {
    private val listStories = MutableLiveData<StoriesResponse>()
    private val errorMessage = MutableLiveData<String>()

    suspend fun getAllStories(location: Int, authorization: String) {
        try {
            val response = ApiConfig.apiInstant.getStoriesWithLocation(location, authorization)
            if (response.isSuccessful) {
                listStories.postValue(response.body())
            } else {
                errorMessage.postValue("Error ${response.errorBody()?.string()}")
            }
        } catch (e: Exception) {
            errorMessage.postValue("Error: ${e.message}")
        }
    }


    fun getListStories(): LiveData<StoriesResponse> {
        return listStories
    }
}