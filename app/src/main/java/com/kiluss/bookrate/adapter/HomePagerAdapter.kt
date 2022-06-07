package com.kiluss.bookrate.adapter

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.kiluss.bookrate.fragment.home.HomeFragment
import com.kiluss.bookrate.fragment.home.MostRateHomeFragment
import com.kiluss.bookrate.fragment.home.AllBookHomeFragment
import com.kiluss.bookrate.fragment.home.TrendingHomeFragment

class HomePagerAdapter(fm: Fragment) : FragmentStateAdapter(fm) {
    enum class TabLayoutEnum{
        MOST_RECENT, TRENDING, MOST_RATE
    }
    override fun getItemCount() = 3

    override fun createFragment(position: Int): Fragment = when(position) {
        TabLayoutEnum.MOST_RECENT.ordinal -> AllBookHomeFragment()
        TabLayoutEnum.TRENDING.ordinal -> TrendingHomeFragment()
        TabLayoutEnum.MOST_RATE.ordinal -> MostRateHomeFragment()
        else -> HomeFragment()
    }
}
