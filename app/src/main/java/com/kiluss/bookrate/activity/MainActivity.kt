package com.kiluss.bookrate.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.navigation.findNavController
import androidx.navigation.ui.setupWithNavController
import com.kiluss.bookrate.R
import com.kiluss.bookrate.databinding.ActivityMainBinding

class MainActivity : AppCompatActivity() {

    private lateinit var binding: ActivityMainBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMainBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        //Initialize the bottom navigation view
        //create bottom navigation view object
        val navFragment = findNavController(R.id.navFragment)
        binding.bottomNavBar.setupWithNavController(navFragment)
    }
}