package com.dicoding.dicodingstory.data.database

import androidx.paging.PagingSource
import androidx.room.Dao
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import com.dicoding.dicodingstory.data.response.ItemStoryResponse

@Dao
interface StoryDao {
    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun insertStory(story: List<ItemStoryResponse>)

    @Query("SELECT * FROM story")
    fun getAllQuote(): PagingSource<Int, ItemStoryResponse>

    @Query("DELETE FROM story")
    suspend fun deleteAll()
}