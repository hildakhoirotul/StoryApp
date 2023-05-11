package com.example.storyapp.ui.viewmodel

import android.content.ContentValues
import android.content.Context
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.storyapp.R
import com.example.storyapp.api.ApiConfig
import com.example.storyapp.model.AddStoryResponse
import com.example.storyapp.utils.reduceFileImage
import kotlinx.coroutines.launch
import okhttp3.MediaType.Companion.toMediaType
import okhttp3.MultipartBody
import okhttp3.RequestBody.Companion.asRequestBody
import okhttp3.RequestBody.Companion.toRequestBody
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.File

class UploadViewModel : ViewModel() {
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    private val _message = MutableLiveData<String>()
    val message: LiveData<String> = _message

    val latitude = MutableLiveData(0.0)
    val longitude = MutableLiveData(0.0)

    val isLocation = MutableLiveData(false)

    fun uploadStory(context: Context, token: String, image: File?, desc: String) {
        _isLoading.value = true
        val file = reduceFileImage(image as File)

        val description = desc.toRequestBody("text/plain".toMediaType())
        val requestImage = file.asRequestBody("image/jpeg".toMediaType())
        val imgMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
            "photo", file.name, requestImage
        )

        viewModelScope.launch {
            val apiService = ApiConfig.getApiService()
            val uploadImage = apiService.uploadStory(token, imgMultipart, description)
            uploadImage.enqueue(object : Callback<AddStoryResponse> {
                override fun onResponse(
                    call: Call<AddStoryResponse>,
                    response: Response<AddStoryResponse>,
                ) {
                    _isLoading.value = false
                    when (response.code()) {
                        401 -> _message.postValue(context.getString(R.string.unauthorized))
                        413 -> _message.postValue(context.getString(R.string.image_size_warning))
                        201 -> _message.postValue(context.getString(R.string.success_upload))
                        else -> _message.postValue("Error ${response.code()} : ${response.message()}")
                    }
                }

                override fun onFailure(call: Call<AddStoryResponse>, t: Throwable) {
                    _isLoading.value = false
                    Log.e(ContentValues.TAG, "onFailure: ${t.message}")
                }
            })
        }

    }

    fun uploadStoryWithLocation(
        context: Context,
        token: String,
        image: File?,
        desc: String,
        lat: String? = null,
        lon: String? = null,
    ) {
        _isLoading.value = true
        val file = reduceFileImage(image as File)

        val description = desc.toRequestBody("text/plain".toMediaType())
        val latitude = lat?.toRequestBody("text/plain".toMediaType())
        val longitude = lon?.toRequestBody("text/plain".toMediaType())
        val requestImage = file.asRequestBody("image/jpeg".toMediaType())
        val imgMultipart: MultipartBody.Part = MultipartBody.Part.createFormData(
            "photo", file.name, requestImage
        )

        viewModelScope.launch {
            val apiService = ApiConfig.getApiService()
            val uploadImage = apiService.uploadStoryWithLocation(
                token, imgMultipart, description, latitude!!, longitude!!
            )
            uploadImage.enqueue(object : Callback<AddStoryResponse> {
                override fun onResponse(
                    call: Call<AddStoryResponse>,
                    response: Response<AddStoryResponse>,
                ) {
                    _isLoading.value = false
                    when (response.code()) {
                        401 -> _message.postValue(context.getString(R.string.unauthorized))
                        413 -> _message.postValue(context.getString(R.string.image_size_warning))
                        201 -> _message.postValue(context.getString(R.string.success_upload))
                        else -> _message.postValue("Error ${response.code()} : ${response.message()}")
                    }
                }

                override fun onFailure(call: Call<AddStoryResponse>, t: Throwable) {
                    _isLoading.value = false
                    Log.e(ContentValues.TAG, "onFailure: ${t.message}")
                }
            })
        }

    }
}