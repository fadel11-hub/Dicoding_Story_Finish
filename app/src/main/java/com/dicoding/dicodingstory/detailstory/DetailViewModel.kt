package com.dicoding.dicodingstory.detailstory

import android.annotation.SuppressLint
import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.dicoding.dicodingstory.R
import com.dicoding.dicodingstory.data.response.DetailResponse
import com.dicoding.dicodingstory.data.response.StoryItem
import com.dicoding.dicodingstory.data.retrofit.ApiConfig
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DetailViewModel(application: Application): AndroidViewModel(application) {

    val detailStory = MutableLiveData<StoryItem>()
    private val errorMessage = MutableLiveData<String>()

    fun setDetailStory(id: String, authorization: String) {
        ApiConfig.apiInstant
            .DetailStories(id, authorization)
            .enqueue(object : Callback<DetailResponse> {
                @SuppressLint("NullSafeMutableLiveData")
                override fun onResponse(
                    call: Call<DetailResponse>,
                    response: Response<DetailResponse>
                ) {
                    if (response.isSuccessful) {
                        detailStory.postValue(response.body()?.items)
                    } else {
                        val errorBody = response.errorBody()?.string()
                        errorMessage.postValue("$errorBody")
                    }
                }

                override fun onFailure(call: Call<DetailResponse>, t: Throwable) {
                    t.message?.let {
                        errorMessage.postValue(R.string.gagal_memuat_data.toString())
                    }
                }
            })
    }

    fun getDetailStory(): LiveData<StoryItem> {
        return detailStory
    }
}