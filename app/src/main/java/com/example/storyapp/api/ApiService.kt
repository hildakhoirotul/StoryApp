package com.example.storyapp.api

import com.example.storyapp.model.*
import okhttp3.MultipartBody
import okhttp3.RequestBody
import retrofit2.Call
import retrofit2.http.*

interface ApiService {
    @POST("login")
    @FormUrlEncoded
    fun requestLogin(
        @Field("email") email: String,
        @Field("password") password: String,
    ): Call<LoginResponse>

    @POST("register")
    @FormUrlEncoded
    fun requestRegister(
        @Field("name") name: String,
        @Field("email") email: String,
        @Field("password") password: String,
    ): Call<RegisterResponse>

    @GET("stories")
    suspend fun getStories(
        @Header("Authorization") token: String,
        @Query("page") page: Int,
        @Query("size") size: Int,
    ): StoriesResponse

    @GET("stories/{id}")
    fun getStoryDetail(
        @Header("Authorization") token: String,
        @Path("id") id: String,
    ): Call<DetailResponse>

    @GET("stories/{id}?location=1")
    fun getStoryLocationDetail(
        @Header("Authorization") token: String,
        @Path("id") id: String,
    ): Call<DetailResponse>

    @Multipart
    @POST("stories")
    fun uploadStory(
        @Header("Authorization") token: String,
        @Part file: MultipartBody.Part,
        @Part("description") description: RequestBody,
    ): Call<AddStoryResponse>

    @GET("stories?location=1")
    fun getStoryListLocation(
        @Header("Authorization") token: String,
        @Query("size") size: Int,
    ): Call<StoriesResponse>

    @Multipart
    @POST("stories?location=1")
    fun uploadStoryWithLocation(
        @Header("Authorization") token: String,
        @Part file: MultipartBody.Part,
        @Part("description") description: RequestBody,
        @Part("lat") lat: RequestBody,
        @Part("lon") lon: RequestBody,
    ): Call<AddStoryResponse>
}