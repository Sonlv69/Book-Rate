package com.kiluss.bookrate.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.Handler
import android.util.Log
import android.widget.Toast
import com.google.android.material.tabs.TabLayoutMediator
import com.kiluss.bookrate.R
import com.kiluss.bookrate.adapter.LoginPagerAdapter
import com.kiluss.bookrate.databinding.ActivityLoginBinding

class LoginActivity : AppCompatActivity() {
    private var backPressPreviousState: Boolean = false
    private lateinit var binding: ActivityLoginBinding
    private lateinit var loginPagerAdapter: LoginPagerAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityLoginBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        loginPagerAdapter = LoginPagerAdapter(this)
        binding.vpLogin.adapter = loginPagerAdapter

        TabLayoutMediator(binding.tlLogin, binding.vpLogin) { tab, position ->
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
            Toast.makeText(this, "Press 1 more time to exit", Toast.LENGTH_SHORT).show()
            Handler().postDelayed({
                backPressPreviousState = false
            }, 3000)
        }
    }
}