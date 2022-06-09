package com.kiluss.bookrate.fragment

import android.os.Bundle
import android.os.Handler
import android.os.Looper
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.kiluss.bookrate.adapter.FollowAdapter
import com.kiluss.bookrate.data.model.FollowModel
import com.kiluss.bookrate.databinding.FragmentUserFollowedBinding

private const val ARG_PARAM = "param"

class UserFollowFragment : Fragment(), FollowAdapter.FollowAdapterInterface {
    private var followList: ArrayList<FollowModel>? = null
    private lateinit var followAdapter: FollowAdapter
    private var _binding: FragmentUserFollowedBinding? = null
    private val binding get() = _binding!!

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            followList = it.getParcelableArrayList(ARG_PARAM)
        }
        followAdapter = context?.let { followList?.let { it1 -> FollowAdapter(it, it1, this) } }!!
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentUserFollowedBinding.inflate(inflater, container, false)
        binding.rcvFollow.layoutManager = LinearLayoutManager(context)
        binding.rcvFollow.adapter = followAdapter
        return binding.root
    }

    companion object {
        @JvmStatic
        fun newInstance(param: ArrayList<FollowModel>) =
            UserFollowFragment().apply {
                arguments = Bundle().apply {
                    putParcelableArrayList(ARG_PARAM, param)
                }
            }
    }

    override fun onFollowClick(adapterPosition: Int, person: FollowModel) {
        //person.isFollowing = !person.isFollowing
        followAdapter.notifyItemChanged(adapterPosition)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
