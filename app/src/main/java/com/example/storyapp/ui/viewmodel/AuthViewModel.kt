package com.example.storyapp.ui.viewmodel

import android.content.ContentValues.TAG
import android.util.Log
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.storyapp.api.ApiConfig
import com.example.storyapp.model.LoginResponse
import com.example.storyapp.model.RegisterResponse
import kotlinx.coroutines.launch
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AuthViewModel : ViewModel() {

    private val _login = MutableLiveData<LoginResponse>()
    val login: LiveData<LoginResponse> = _login

    private val _register = MutableLiveData<RegisterResponse>()
    val register: LiveData<RegisterResponse> = _register

    private val _isLoading = MutableLiveData<Boolean>()
    val isLoading: LiveData<Boolean> = _isLoading

    val tempEmail = MutableLiveData("")
    val error = MutableLiveData("")

    fun getLogin(email: String, password: String) {
        tempEmail.postValue(email)
        _isLoading.value = true
        viewModelScope.launch {
            val client = ApiConfig.getApiService().requestLogin(email, password)
            client.enqueue(object : Callback<LoginResponse> {
                override fun onResponse(
                    call: Call<LoginResponse>,
                    response: Response<LoginResponse>,
                ) {
                    _isLoading.value = false
                    if (response.isSuccessful) {
                        _login.value = response.body()
                    } else {
                        Log.e(TAG, "onFailure: ${response.message()}")
                        error.postValue(response.message().toString())
                    }
                }

                override fun onFailure(call: Call<LoginResponse>, t: Throwable) {
                    _isLoading.value = false
                    Log.e(TAG, "onFailure: ${t.message}")
                    error.postValue(t.message.toString())
                }
            })
        }
    }

    fun getRegister(name: String, email: String, password: String) {
        _isLoading.value = true
        viewModelScope.launch {
            val client = ApiConfig.getApiService().requestRegister(name, email, password)
            client.enqueue(object : Callback<RegisterResponse> {
                override fun onResponse(
                    call: Call<RegisterResponse>,
                    response: Response<RegisterResponse>,
                ) {
                    _isLoading.value = false
                    if (response.isSuccessful) {
                        _register.value = response.body()
                    } else {
                        Log.e(TAG, "onFailure: ${response.message()}")
                        error.value = response.message()
                    }
                }

                override fun onFailure(call: Call<RegisterResponse>, t: Throwable) {
                    _isLoading.value = false
                    Log.e(TAG, "onFailure: ${t.message}")
                    error.value = t.message
                }
            })
        }
    }
}