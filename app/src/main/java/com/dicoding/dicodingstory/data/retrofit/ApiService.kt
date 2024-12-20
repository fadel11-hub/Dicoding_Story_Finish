package com.dicoding.dicodingstory.data.retrofit

import com.dicoding.dicodingstory.data.response.DetailResponse
import com.dicoding.dicodingstory.data.response.LoginResponse
import com.dicoding.dicodingstory.data.response.SignupResponse
import com.dicoding.dicodingstory.data.response.StoriesResponse
import com.dicoding.dicodingstory.data.response.StoryResponse
import com.dicoding.dicodingstory.data.response.UploadResponse
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.Response
import retrofit2.http.Field
import retrofit2.http.FormUrlEncoded
import retrofit2.http.GET
import retrofit2.http.Header
import retrofit2.http.Multipart
import retrofit2.http.POST
import retrofit2.http.Part
import retrofit2.http.Path
import retrofit2.http.Query

interface ApiService {
    @FormUrlEncoded
    @POST("login")
    fun login(
        @Field("email") email: String,
        @Field("password") password: String
    ): Call<LoginResponse>

    @FormUrlEncoded
    @POST("register")
    fun register(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String
    ): Call<SignupResponse>

    @GET("stories")
    suspend fun getAllStories(
        @Query("page") page: Int,
        @Query("size") size: Int,
        @Header("Authorization") authHeader: String
    ): Response<StoryResponse>

    @GET("stories/{id}")
    fun DetailStories(
        @Path("id") username: String,
        @Header("Authorization") authorization: String,
    ): Call<DetailResponse>

    @Multipart
    @POST("stories")
    suspend fun AddStory(
        @Part file: MultipartBody.Part,
        @Part("description") description: RequestBody,
        @Header("Authorization") authorization: String,
    ): UploadResponse

    @GET("stories")
    suspend fun getStoriesWithLocation(
        @Query("location") location : Int,
        @Header("Authorization") authorization: String,
    ): Response<StoriesResponse>
}