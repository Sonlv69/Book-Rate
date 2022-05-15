package com.kiluss.bookrate.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import com.kiluss.bookrate.databinding.ActivityPersonalDetailBinding

class PersonalDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPersonalDetailBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPersonalDetailBinding.inflate(layoutInflater)
        val view = binding.root
        setContentView(view)
        binding.ivEdit.setOnClickListener {
            startActivity(
                Intent(this, PersonalDetailEditActivity::class.java)
            )
        }

    }
}