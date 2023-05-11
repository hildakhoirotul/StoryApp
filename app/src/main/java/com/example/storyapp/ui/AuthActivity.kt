package com.example.storyapp.ui

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.WindowManager
import androidx.appcompat.app.AppCompatActivity
import com.example.storyapp.R
import com.example.storyapp.databinding.ActivityAuthBinding
import com.example.storyapp.utils.Preferences

@Suppress("DEPRECATION")
class AuthActivity : AppCompatActivity() {

    private lateinit var binding: ActivityAuthBinding
    private lateinit var pref: Preferences

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthBinding.inflate(layoutInflater)
        setContentView(binding.root)

        window.setFlags(
            WindowManager.LayoutParams.FLAG_FULLSCREEN, WindowManager.LayoutParams.FLAG_FULLSCREEN
        )

        pref = Preferences(this)

        if (pref.token != "") {
            routeToMainActivity()
        } else {
            val fragmentManager = supportFragmentManager
            val loginFragment = LoginFragment()
            val fragment = fragmentManager.findFragmentByTag(LoginFragment::class.java.simpleName)

            if (fragment !is LoginFragment) {
                Log.d(
                    "MyFlexibleFragment", "Fragment Name :" + LoginFragment::class.java.simpleName
                )
                fragmentManager.beginTransaction().replace(
                        R.id.frame_container,
                        loginFragment,
                        LoginFragment::class.java.simpleName
                    ).commit()
            }
        }
    }

    private fun routeToMainActivity() {
        val intent = Intent(this@AuthActivity, MainActivity::class.java)
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP or Intent.FLAG_ACTIVITY_NEW_TASK)
        startActivity(intent)
        finish()
    }
}