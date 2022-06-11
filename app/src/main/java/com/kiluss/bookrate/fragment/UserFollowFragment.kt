package com.kiluss.bookrate.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.LinearLayoutManager
import com.kiluss.bookrate.activity.UserDetailActivity
import com.kiluss.bookrate.adapter.FollowAdapter
import com.kiluss.bookrate.data.model.FollowModel
import com.kiluss.bookrate.databinding.FragmentUserFollowedBinding
import com.kiluss.bookrate.utils.Constants

private const val ARG_PARAM_1 = "param_1"
private const val ARG_PARAM_2 = "param_2"

class UserFollowFragment : Fragment(), FollowAdapter.FollowAdapterInterface {
    private var followList: ArrayList<FollowModel>? = null
    private lateinit var followAdapter: FollowAdapter
    private var _binding: FragmentUserFollowedBinding? = null
    private val binding get() = _binding!!
    private var isFollowers = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.let {
            followList = it.getParcelableArrayList(ARG_PARAM_1)
            isFollowers = it.getBoolean(ARG_PARAM_2)
        }
        followAdapter = context?.let { followList?.let { it1 -> FollowAdapter(it, it1, this, isFollowers) } }!!
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
        fun newInstance(param: ArrayList<FollowModel>, isFollow: Boolean) =
            UserFollowFragment().apply {
                arguments = Bundle().apply {
                    putParcelableArrayList(ARG_PARAM_1, param)
                    putBoolean(ARG_PARAM_2, isFollow)
                }
            }
    }

    override fun onFollowClick(id: Int) {
        val intent = Intent(requireContext(), UserDetailActivity::class.java)
        intent.putExtra(Constants.EXTRA_MESSAGE, id)
        startActivity(intent)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
