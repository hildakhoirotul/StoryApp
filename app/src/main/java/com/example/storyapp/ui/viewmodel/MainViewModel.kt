package com.example.storyapp.ui.viewmodel

import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.storyapp.api.ApiConfig
import com.example.storyapp.model.ListStoryItem
import com.example.storyapp.model.StoriesResponse
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainViewModel : ViewModel() {


    fun getStoryLocation(token: String): LiveData<List<ListStoryItem>> {
        val story = MutableLiveData<List<ListStoryItem>>()
        viewModelScope.launch {
            val client = ApiConfig.getApiService().getStoryListLocation(token, 100)
            client.enqueue(object : Callback<StoriesResponse> {
                override fun onResponse(
                    call: Call<StoriesResponse>,
                    response: Response<StoriesResponse>,
                ) {
                    if (response.isSuccessful) {
                        story.value = response.body()?.listStory
                    } else {
                        Log.e(TAG, "onFailure: ${response.message()}")
                    }
                }

                override fun onFailure(call: Call<StoriesResponse>, t: Throwable) {
                    Log.e(TAG, "onFailure: ${t.message}")
                }
            })
        }
        return story
    }

    companion object {
        private const val TAG = "HomeViewModel"
    }
}