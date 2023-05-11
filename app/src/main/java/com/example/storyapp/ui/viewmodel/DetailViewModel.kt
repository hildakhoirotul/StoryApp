package com.example.storyapp.ui.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.storyapp.api.ApiConfig
import com.example.storyapp.model.DetailResponse
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class DetailViewModel : ViewModel() {
    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    val error = MutableLiveData("")

    fun getDetailWithLocation(token: String, id: String): LiveData<DetailResponse> {
        _isLoading.value = true
        val detail = MutableLiveData<DetailResponse>()
        viewModelScope.launch {
            val client = ApiConfig.getApiService().getStoryLocationDetail(token, id)
            client.enqueue(object : Callback<DetailResponse> {
                override fun onResponse(
                    call: Call<DetailResponse>,
                    response: Response<DetailResponse>,
                ) {
                    _isLoading.value = false
                    if (response.isSuccessful) {
                        detail.value = response.body()
                    } else {
                        Log.e(TAG, "onFailure: ${response.message()}")
                        error.postValue(response.message().toString())
                    }
                }

                override fun onFailure(call: Call<DetailResponse>, t: Throwable) {
                    _isLoading.value = false
                    Log.e(TAG, "onFailure: ${t.message}")
                    error.postValue(t.message.toString())
                }
            })
        }
        return detail
    }

    companion object {
        private const val TAG = "DetailViewModel"
    }
}