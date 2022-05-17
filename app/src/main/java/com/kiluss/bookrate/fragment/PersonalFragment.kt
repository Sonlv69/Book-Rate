package com.kiluss.bookrate.fragment

import android.content.Context.MODE_PRIVATE
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AppCompatDelegate
import androidx.fragment.app.Fragment
import com.kiluss.bookrate.activity.PersonalDetailActivity
import com.kiluss.bookrate.activity.PersonalDetailEditActivity
import com.kiluss.bookrate.databinding.FragmentPersonalBinding
import com.kiluss.bookrate.utils.Const.Companion.NIGHT_MODE


class PersonalFragment : Fragment() {
    private var _binding: FragmentPersonalBinding? = null
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPersonalBinding.inflate(inflater, container, false)
        val sharedPreferences = requireContext().getSharedPreferences(NIGHT_MODE, MODE_PRIVATE)
        binding.darkModeSwitch.isChecked = sharedPreferences.getBoolean(NIGHT_MODE, false)
        binding.rlPersonalDetail.setOnClickListener {
            requireActivity().startActivity(
                Intent(
                    requireActivity(),
                    PersonalDetailActivity::class.java
                )
            )
        }

        binding.darkModeSwitch.setOnCheckedChangeListener { _, _ ->
            val checkedItem = binding.darkModeSwitch.isChecked
            if (checkedItem) {
                AppCompatDelegate
                    .setDefaultNightMode(
                        AppCompatDelegate.MODE_NIGHT_YES
                    )
            } else {
                AppCompatDelegate
                    .setDefaultNightMode(
                        AppCompatDelegate.MODE_NIGHT_NO
                    )
            }
            sharedPreferences.edit().putBoolean(NIGHT_MODE, checkedItem).apply()
        }

        binding.ivButtonToPersonalPage.setOnClickListener {
            startActivity(
                Intent(requireContext(), PersonalDetailEditActivity::class.java)
            )
        }

        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}