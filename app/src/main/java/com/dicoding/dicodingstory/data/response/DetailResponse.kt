package com.dicoding.dicodingstory.data.response

import com.google.gson.annotations.SerializedName

data class DetailResponse(
    @SerializedName("story")
    val items : StoryItem
)