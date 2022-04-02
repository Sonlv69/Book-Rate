package com.kiluss.bookrate.adapter

import androidx.fragment.app.Fragment
import androidx.fragment.app.FragmentActivity
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.kiluss.bookrate.fragment.home.HomeFragment
import com.kiluss.bookrate.fragment.home.MostRateHomeFragment
import com.kiluss.bookrate.fragment.home.TrendingHomeFragment

class HomePagerAdapter(fm: Fragment) : FragmentStateAdapter(fm) {
    override fun getItemCount(): Int = 2

    override fun createFragment(position: Int): Fragment = when(position) {
        0 -> TrendingHomeFragment()
        1 -> MostRateHomeFragment()
        else -> HomeFragment()
    }
}