package com.example.storyapp.utils

import android.content.Context
import com.example.storyapp.R
import com.example.storyapp.api.ApiConfig
import com.example.storyapp.data.StoryRepository
import com.example.storyapp.database.StoryDatabase
import java.text.ParseException
import java.text.SimpleDateFormat
import java.util.*

object Setting {
    private lateinit var pref: Preferences

    val emailFormat = Regex("[a-zA-Z0-9._-]+@[a-z]+\\.+[a-z]+")
    private const val timestampFormat = "yyyy-MM-dd'T'HH:mm:ss.SSS'Z'"

    private fun getCurrentDate(): Date {
        return Date()
    }

    private fun parseUTCDate(timestamp: String): Date {
        return try {
            val formatter = SimpleDateFormat(timestampFormat, Locale.getDefault())
            formatter.timeZone = TimeZone.getTimeZone("UTC")
            formatter.parse(timestamp) as Date
        } catch (e: ParseException) {
            getCurrentDate()
        }
    }

    fun getStoryTime(context: Context, timestamp: String): String {
        val currentTime = getCurrentDate()
        val storyTime = parseUTCDate(timestamp)
        val diff: Long = currentTime.time - storyTime.time
        val seconds = diff / 1000
        val minutes = seconds / 60
        val hours = minutes / 60
        val days = hours / 24
        val label = when (minutes.toInt()) {
            0 -> "$seconds ${context.getString(R.string.seconds)}"
            in 1..59 -> "$minutes ${context.getString(R.string.minutes)}"
            in 60..1440 -> "$hours ${context.getString(R.string.hours)}"
            else -> "$days ${context.getString(R.string.days)}"
        }
        return label
    }

    fun provideRepository(context: Context): StoryRepository {
        pref = Preferences(context)
        val database = StoryDatabase.getDatabase(context)
        val apiService = ApiConfig.getApiService()
        val token = "Bearer " + pref.token.toString()
        return StoryRepository(database, apiService, token)
    }
}