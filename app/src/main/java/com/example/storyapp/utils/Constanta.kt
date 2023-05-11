package com.example.storyapp.utils

import android.Manifest

object Constanta {

    const val PREFS_NAME = "user_pref"
    const val TOKEN = "key.token"
    const val USER_ID = "key.user.id"
    const val NAME = "key.user.name"
    const val IS_LOGIN = "key.isLogin"
    const val EMAIL = "key.email"
    const val EXTRA_STORY = "extra_story"
    const val CAMERA_X_RESULT = 200
    val REQUIRED_PERMISSIONS = arrayOf(Manifest.permission.CAMERA)
    const val REQUEST_CODE_PERMISSIONS = 10
    const val REQUEST_LOCATION_PERMISSIONS = 30
    const val LATITUDE = "latitude"
    const val LONGITUDE = "longitude"

    const val token = "Bearer $TOKEN"
}