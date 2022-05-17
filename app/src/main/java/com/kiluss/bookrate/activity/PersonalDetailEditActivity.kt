package com.kiluss.bookrate.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.kiluss.bookrate.databinding.ActivityPersonalDetailEditBinding

class PersonalDetailEditActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPersonalDetailEditBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPersonalDetailEditBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.btnSave.setOnClickListener{
            finish()
        }
    }
}