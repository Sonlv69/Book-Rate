package com.kiluss.bookrate.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.kiluss.bookrate.databinding.ActivityChangePasswordBinding

class ChangePasswordActivity : AppCompatActivity() {
    private lateinit var binding: ActivityChangePasswordBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChangePasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnSubmit.setOnClickListener{
            finish()
        }
    }
}