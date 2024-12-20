package com.dicoding.dicodingstory.data.response

import android.os.Parcelable
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import kotlinx.android.parcel.Parcelize

data class StoryResponse(
    val error: Boolean,
    val message: String,
    val listStory: List<ItemStoryResponse>
)

@Entity(tableName = "story")
@Parcelize
data class ItemStoryResponse(

    @PrimaryKey
    @field:SerializedName("id")
    val id: String,

    @field:SerializedName("name")
    val name: String? = null,

    @field:SerializedName("description")
    val description: String? = null,

    @field:SerializedName("photoUrl")
    val photoUrl: String? = null,

    @field:SerializedName("createdAt")
    val createdAt: String? = null
) : Parcelable