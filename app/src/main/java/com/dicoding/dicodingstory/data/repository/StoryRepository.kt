package com.dicoding.dicodingstory.data.repository

import androidx.lifecycle.LiveData
import androidx.paging.ExperimentalPagingApi
import androidx.paging.Pager
import androidx.paging.PagingConfig
import androidx.paging.PagingData
import androidx.paging.liveData
import com.dicoding.dicodingstory.data.database.StoryDatabase
import com.dicoding.dicodingstory.data.pref.UserPreferences
import com.dicoding.dicodingstory.data.response.ItemStoryResponse
import com.dicoding.dicodingstory.data.retrofit.ApiService

interface IStoryRepository {
    fun getQuote(): LiveData<PagingData<ItemStoryResponse>>
}

class StoryRepository(
    private val storyDatabase: StoryDatabase,
    private val apiService: ApiService,
    private val userPreference: UserPreferences,
    private val pagingConfig: PagingConfig = PagingConfig(pageSize = 5)
) : IStoryRepository {

    @OptIn(ExperimentalPagingApi::class)
    override fun getQuote(): LiveData<PagingData<ItemStoryResponse>> {
        return Pager(
            config = pagingConfig,
            remoteMediator = StoryRemoteMediator(storyDatabase, apiService, userPreference),
            pagingSourceFactory = {
                storyDatabase.storyDao().getAllQuote()
            }
        ).liveData
    }
}