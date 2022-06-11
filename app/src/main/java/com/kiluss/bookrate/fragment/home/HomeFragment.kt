package com.kiluss.bookrate.fragment.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import com.google.android.material.tabs.TabLayoutMediator
import com.kiluss.bookrate.adapter.HomePagerAdapter
import com.kiluss.bookrate.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {
    private var _binding: FragmentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var homePagerAdapter: HomePagerAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val view = binding.root

        homePagerAdapter = HomePagerAdapter(this)
        binding.viewPagerHome.adapter = homePagerAdapter

        TabLayoutMediator(binding.tabLayoutHome, binding.viewPagerHome) { tab, position ->
            when(position) {
                1 -> tab.text = "Most Review"
                2 -> tab.text = "Top Rate"
                3 -> tab.text = "Top Read"
                else -> tab.text = "All books"
            }
        }.attach()
        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}