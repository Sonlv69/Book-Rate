package com.kiluss.bookrate.adapter

import androidx.fragment.app.Fragment
import androidx.viewpager2.adapter.FragmentStateAdapter
import com.kiluss.bookrate.fragment.home.*

class HomePagerAdapter(fm: Fragment) : FragmentStateAdapter(fm) {
    enum class TabLayoutEnum{
        ALL_BOOK, TOP_REVIEW, TOP_RATE_AVG
    }
    override fun getItemCount() = 4

    override fun createFragment(position: Int): Fragment = when(position) {
        TabLayoutEnum.ALL_BOOK.ordinal -> AllBookHomeFragment()
        TabLayoutEnum.TOP_REVIEW.ordinal -> TopReviewHomeFragment()
        TabLayoutEnum.TOP_RATE_AVG.ordinal -> TopRateAvgHomeFragment()
        else -> TopReadHomeFragment()
    }
}
