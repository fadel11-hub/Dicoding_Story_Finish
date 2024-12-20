package com.dicoding.dicodingstory.data.response

import com.google.gson.annotations.SerializedName

data class StoriesResponse(
    @SerializedName("listStory")
    val items : ArrayList<StoryItem>
)