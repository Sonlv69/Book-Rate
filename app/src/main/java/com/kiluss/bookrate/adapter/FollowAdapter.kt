package com.kiluss.bookrate.adapter

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.kiluss.bookrate.R
import com.kiluss.bookrate.data.model.FollowModel
import com.kiluss.bookrate.databinding.ItemFollowBinding

class FollowAdapter(
    private val context: Context,
    private val followLists: List<FollowModel>,
    private val followAdapterAdapterInterface: FollowAdapterInterface
) : RecyclerView.Adapter<FollowAdapter.ViewHolder>() {

    interface FollowAdapterInterface {
        fun onFollowClick(adapterPosition: Int, person: FollowModel)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemFollowBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindView(followLists[position])
    }

    override fun getItemCount(): Int {
        return followLists.size
    }

    inner class ViewHolder(
        val binding: ItemFollowBinding,
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bindView(person: FollowModel) {
            Log.e("followAdapter", person.toString())
            binding.ivAvatar.let {

            }
            binding.tvName.text = person.iDFollower.toString()
            //binding.tvFollower.text = "${person.follower} followers"
//            if (!person.isFollowing) {
//                binding.btnFollowState.background = context.getDrawable(R.drawable.button_bg_outlined)
//                binding.btnFollowState.text = "Follow"
//            } else {
//                binding.btnFollowState.background = context.getDrawable(R.drawable.button_bg)
//                binding.btnFollowState.text = "Following"
//            }
//            binding.btnFollowState.setOnClickListener {
//                followAdapterAdapterInterface.onFollowClick(adapterPosition, person)
//            }
        }
    }
}
