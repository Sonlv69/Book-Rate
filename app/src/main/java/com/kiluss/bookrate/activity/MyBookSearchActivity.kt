package com.kiluss.bookrate.activity

import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import androidx.appcompat.widget.PopupMenu
import androidx.core.content.ContentProviderCompat.requireContext
import com.kiluss.bookrate.databinding.ActivityLoginBinding
import com.kiluss.bookrate.databinding.ActivityMyBookSearchBinding

class MyBookSearchActivity : AppCompatActivity() {
    private lateinit var binding: ActivityMyBookSearchBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityMyBookSearchBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.hide()
        binding.llBookState.setOnClickListener {
            showOverflowMenuSearchBookState()
        }
    }

    private fun showOverflowMenuSearchBookState() {
        val menu = PopupMenu(this, binding.llBookState)
        menu.menu.apply {
            add("All State").setOnMenuItemClickListener {
                binding.tvBookState.text = "All State"
                true
            }
            add("Read").setOnMenuItemClickListener {
                binding.tvBookState.text = "Read"
                true
            }
            add("Currently Reading").setOnMenuItemClickListener {
                binding.tvBookState.text ="Currently Reading"
                true
            }

            add("Want To Read").setOnMenuItemClickListener {
                binding.tvBookState.text ="Want To Read"
                true
            }
        }
        menu.show()
    }
}