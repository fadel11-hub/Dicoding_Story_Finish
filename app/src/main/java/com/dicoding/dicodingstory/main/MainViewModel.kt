package com.dicoding.dicodingstory.main

import androidx.lifecycle.LiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.asLiveData
import androidx.lifecycle.viewModelScope
import androidx.paging.PagingData
import androidx.paging.cachedIn
import com.dicoding.dicodingstory.data.pref.UserModel
import com.dicoding.dicodingstory.data.repository.StoryRepository
import com.dicoding.dicodingstory.data.repository.UserRepository
import com.dicoding.dicodingstory.data.response.ItemStoryResponse

class MainViewModel(storyRepository: StoryRepository, private val repository: UserRepository) : ViewModel() {

    fun getSession(): LiveData<UserModel> {
        return repository.getSession().asLiveData()
    }

    val story: LiveData<PagingData<ItemStoryResponse>> =
        storyRepository.getQuote().cachedIn(viewModelScope)
}