package com.kiluss.bookrate.activity

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.kiluss.bookrate.R
import com.kiluss.bookrate.data.model.FollowModel
import com.kiluss.bookrate.databinding.ActivityPersonalDetailBinding
import com.kiluss.bookrate.fragment.UserFollowFragment
import com.kiluss.bookrate.utils.Const.Companion.FOLLOWED
import com.kiluss.bookrate.utils.Const.Companion.FOLLOWING

class PersonalDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPersonalDetailBinding
    private lateinit var followList: List<FollowModel>

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPersonalDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        followList = arrayListOf(
            FollowModel("", "KiluSs", 12, true),
            FollowModel("", "KiluSs", 12, false),
            FollowModel("", "KiluSs", 12, true),
            FollowModel("", "KiluSs", 12, false),
            FollowModel("", "KiluSs", 12, true),
            FollowModel("", "KiluSs", 12, false),
        )
        binding.ivEdit.setOnClickListener {
            startActivity(
                Intent(this, PersonalDetailEditActivity::class.java)
            )
        }
        binding.tvFollowed.setOnClickListener {
            addFragmentToActivity(
                UserFollowFragment.newInstance(followList as ArrayList<FollowModel>),
                UserFollowFragment().toString()
            )
            supportActionBar?.title = FOLLOWED
        }

        binding.tvFollowing.setOnClickListener {
            addFragmentToActivity(
                UserFollowFragment.newInstance(followList as ArrayList<FollowModel>),
                UserFollowFragment().toString()
            )
            supportActionBar?.title = FOLLOWING
        }
    }

    private fun addFragmentToActivity(fragment: Fragment?, name: String) {
        if (fragment == null) return
        val fm = supportFragmentManager
        val tr = fm.beginTransaction()
        tr.add(R.id.root_container, fragment, name)
        tr.addToBackStack(name)
        tr.commitAllowingStateLoss()
    }
}