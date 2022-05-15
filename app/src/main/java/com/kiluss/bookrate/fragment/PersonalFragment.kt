package com.kiluss.bookrate.fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import com.kiluss.bookrate.activity.MainActivity
import com.kiluss.bookrate.activity.PersonalDetailActivity
import com.kiluss.bookrate.databinding.FragmentPersonalBinding

class PersonalFragment : Fragment() {

    private var _binding: FragmentPersonalBinding? = null

    // This property is only valid between onCreateView and onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentPersonalBinding.inflate(inflater, container, false)
        binding.rlPersonalDetail.setOnClickListener {
            requireActivity().startActivity(
                Intent(
                    requireActivity(),
                    PersonalDetailActivity::class.java
                )
            )
        }


        return binding.root
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }


}