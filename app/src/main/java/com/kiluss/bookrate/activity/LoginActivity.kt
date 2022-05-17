package com.kiluss.bookrate.activity

import android.annotation.SuppressLint
import android.content.Intent
import android.net.Uri
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.Toast
import androidx.appcompat.app.AppCompatDelegate
import androidx.viewpager2.widget.ViewPager2
import com.google.android.material.tabs.TabLayoutMediator
import com.kiluss.bookrate.R
import com.kiluss.bookrate.adapter.LoginPagerAdapter
import com.kiluss.bookrate.databinding.ActivityLoginBinding
import com.kiluss.bookrate.utils.Const
import com.kiluss.bookrate.utils.Const.Companion.NIGHT_MODE
import kotlin.math.log

class LoginActivity : AppCompatActivity() {
    private var backPressPreviousState: Boolean = false
    private lateinit var binding: ActivityLoginBinding
    private lateinit var loginPagerAdapter: LoginPagerAdapter
    lateinit var viewPager: ViewPager2
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val sharedPreferences = getSharedPreferences(NIGHT_MODE, MODE_PRIVATE)
        if (sharedPreferences.getBoolean(NIGHT_MODE, false)) {
            AppCompatDelegate
                .setDefaultNightMode(
                    AppCompatDelegate.MODE_NIGHT_YES
                )
        } else {
            AppCompatDelegate
                .setDefaultNightMode(
                    AppCompatDelegate.MODE_NIGHT_NO
                )
        }
        viewPager = binding.vpLogin
        loginPagerAdapter = LoginPagerAdapter(this)
        viewPager.adapter = loginPagerAdapter

        TabLayoutMediator(binding.tlLogin, viewPager) { tab, position ->
            when(position) {
                0 -> tab.text = "Login"
                1 -> tab.text = "Sign up"
            }
        }.attach()
        supportActionBar?.hide()
    }

    override fun onBackPressed() {
        if (backPressPreviousState) {
            super.onBackPressed()
        } else {
            backPressPreviousState = true
            Toast.makeText(this, "Press one more time to exit", Toast.LENGTH_SHORT).show()
            Handler().postDelayed({
                backPressPreviousState = false
            }, 3000)
        }
    }
}