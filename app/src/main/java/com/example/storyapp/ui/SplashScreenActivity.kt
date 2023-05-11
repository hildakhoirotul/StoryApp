package com.example.storyapp.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import com.example.storyapp.R
import com.example.storyapp.databinding.ActivitySplashScreenBinding
import com.example.storyapp.utils.Preferences
import java.util.*
import kotlin.concurrent.schedule

class SplashScreenActivity : AppCompatActivity() {

    private lateinit var binding: ActivitySplashScreenBinding
    private lateinit var pref: Preferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivitySplashScreenBinding.inflate(layoutInflater)
        setContentView(binding.root)

        pref = Preferences(this)

        if (pref.token != "") Timer().schedule(2000) {
            startActivity(Intent(this@SplashScreenActivity, MainActivity::class.java))
            finish()
        } else Timer().schedule(2000) {
            val fragmentManager = supportFragmentManager
            val loginFragment = LoginFragment()
            val fragment = fragmentManager.findFragmentByTag(LoginFragment::class.java.simpleName)

            if (fragment !is LoginFragment) {
                Log.d(
                    "MyFlexibleFragment", "Fragment Name :" + LoginFragment::class.java.simpleName
                )
                fragmentManager.beginTransaction().replace(
                        R.id.frame_container, loginFragment, LoginFragment::class.java.simpleName
                    ).commit()
            }
        }
    }
}