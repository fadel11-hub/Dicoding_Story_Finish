package com.dicoding.dicodingstory.data.repository

import androidx.paging.*
import androidx.room.withTransaction
import com.dicoding.dicodingstory.data.database.RemoteKeys
import com.dicoding.dicodingstory.data.database.StoryDatabase
import com.dicoding.dicodingstory.data.pref.UserPreferences
import com.dicoding.dicodingstory.data.response.ItemStoryResponse
import com.dicoding.dicodingstory.data.response.StoryResponse
import com.dicoding.dicodingstory.data.retrofit.ApiService
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import retrofit2.Response

@OptIn(ExperimentalPagingApi::class)
class StoryRemoteMediator(
    private val database: StoryDatabase,
    private val apiService: ApiService,
    private val userPreferences: UserPreferences // Pastikan nama parameter sesuai
) : RemoteMediator<Int, ItemStoryResponse>() {

    private companion object {
        const val INITIAL_PAGE_INDEX = 1
    }

    override suspend fun initialize(): InitializeAction {
        return InitializeAction.LAUNCH_INITIAL_REFRESH
    }

    override suspend fun load(
        loadType: LoadType,
        state: PagingState<Int, ItemStoryResponse>
    ): MediatorResult {
        val page = when (loadType) {
            LoadType.REFRESH -> {
                val remoteKeys = getRemoteKeyClosestToCurrentPosition(state)
                remoteKeys?.nextKey?.minus(1) ?: INITIAL_PAGE_INDEX
            }
            LoadType.PREPEND -> {
                val remoteKeys = getRemoteKeyForFirstItem(state)
                val prevKey = remoteKeys?.prevKey
                    ?: return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
                prevKey
            }
            LoadType.APPEND -> {
                val remoteKeys = getRemoteKeyForLastItem(state)
                val nextKey = remoteKeys?.nextKey
                    ?: return MediatorResult.Success(endOfPaginationReached = remoteKeys != null)
                nextKey
            }
        }

        // Mengambil token dari UserPreferences
        val token = userPreferences.getToken()

        // Menangani jika token kosong atau null
        if (token.isNullOrEmpty()) {
            return MediatorResult.Error(Exception("Token is null or empty"))
        }

        // Memanggil API untuk mendapatkan data
        try {
            val response: Response<StoryResponse> = apiService.getAllStories(page, state.config.pageSize, "Bearer $token")
            if (response.isSuccessful) {
                val responseData = response.body()
                val listStory = responseData?.listStory ?: emptyList()
                val endOfPaginationReached = listStory.isEmpty()

                // Menyimpan data dan remote keys ke dalam database
                database.withTransaction {
                    if (loadType == LoadType.REFRESH) {
                        database.remoteKeysDao().deleteRemoteKeys()
                        database.storyDao().deleteAll()
                    }
                    val prevKey = if (page == 1) null else page - 1
                    val nextKey = if (endOfPaginationReached) null else page + 1
                    val keys = listStory.map {
                        RemoteKeys(id = it.id, prevKey = prevKey, nextKey = nextKey)
                    }
                    // Menyimpan remote keys dan stories ke dalam database
                    withContext(Dispatchers.IO) {
                        database.remoteKeysDao().insertAll(keys)
                        database.storyDao().insertStory(listStory)
                    }
                }

                return MediatorResult.Success(endOfPaginationReached = endOfPaginationReached)
            } else {
                return MediatorResult.Error(Exception("API request failed with code: ${response.code()}."))
            }
        } catch (exception: Exception) {
            return MediatorResult.Error(exception)
        }
    }

    private suspend fun getRemoteKeyForLastItem(state: PagingState<Int, ItemStoryResponse>): RemoteKeys? {
        return state.pages.lastOrNull { it.data.isNotEmpty() }?.data?.lastOrNull()?.let { data ->
            database.remoteKeysDao().getRemoteKeysId(data.id)
        }
    }

    private suspend fun getRemoteKeyClosestToCurrentPosition(state: PagingState<Int, ItemStoryResponse>): RemoteKeys? {
        return state.anchorPosition?.let { position ->
            state.closestItemToPosition(position)?.id?.let { id ->
                database.remoteKeysDao().getRemoteKeysId(id)
            }
        }
    }

    private suspend fun getRemoteKeyForFirstItem(state: PagingState<Int, ItemStoryResponse>): RemoteKeys? {
        return state.pages.firstOrNull { it.data.isNotEmpty() }?.data?.firstOrNull()?.let { data ->
            database.remoteKeysDao().getRemoteKeysId(data.id)
        }
    }
}