package com.kiluss.bookrate.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import com.kiluss.bookrate.databinding.ActivityPersonalDetailEditBinding

class PersonalDetailEditActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPersonalDetailEditBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPersonalDetailEditBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)

        binding.btnSave.setOnClickListener{
            finish()
        }
    }
}