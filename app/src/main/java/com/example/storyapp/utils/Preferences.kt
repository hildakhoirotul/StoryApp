package com.example.storyapp.utils

import android.content.Context
import android.content.SharedPreferences
import com.example.storyapp.utils.Constanta.EMAIL
import com.example.storyapp.utils.Constanta.NAME
import com.example.storyapp.utils.Constanta.PREFS_NAME
import com.example.storyapp.utils.Constanta.TOKEN
import com.example.storyapp.utils.Constanta.USER_ID

class Preferences(context: Context) {

    private var pref: SharedPreferences =
        context.applicationContext.getSharedPreferences(PREFS_NAME, Context.MODE_PRIVATE)
    private val editor = pref.edit()

    fun setStringPreference(key: String, value: String) {
        editor.putString(key, value)
        editor.apply()
    }

    fun setBooleanPreference(key: String, value: Boolean) {
        editor.putBoolean(key, value)
        editor.apply()
    }

    fun clearPreference() {
        editor.clear().apply()
    }

    val token = pref.getString(TOKEN, "")
    val userId = pref.getString(USER_ID, "")
    val name = pref.getString(NAME, "")
    val email = pref.getString(EMAIL, "")
}