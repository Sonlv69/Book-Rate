package com.kiluss.bookrate.fragment.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.google.android.material.tabs.TabLayoutMediator
import com.kiluss.bookrate.R
import com.kiluss.bookrate.adapter.HomePagerAdapter
import com.kiluss.bookrate.databinding.FragmentHomeBinding

class HomeFragment : Fragment() {

    private var _binding: FragmentHomeBinding? = null
    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!
    private lateinit var homePagerAdapter: HomePagerAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        // Inflate the layout for this fragment
        _binding = FragmentHomeBinding.inflate(inflater, container, false)
        val view = binding.root

        homePagerAdapter = HomePagerAdapter(this)
        binding.viewPagerHome.adapter = homePagerAdapter

        TabLayoutMediator(binding.tabLayoutHome, binding.viewPagerHome) { tab, position ->
            when(position) {
                0 -> tab.text = "Popular"
                1 -> tab.text = "Most Rate"
            }
        }.attach()
        return view
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}