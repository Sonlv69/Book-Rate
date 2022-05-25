package com.kiluss.bookrate.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.kiluss.bookrate.R
import com.kiluss.bookrate.data.model.CommentModel
import com.kiluss.bookrate.databinding.ItemCommentBinding

class CommentAdapter(
    private val context: Context,
    private val commentLists: List<CommentModel>,
    private val commentAdapterAdapterInterface: CommentAdapterAdapterInterface
) : RecyclerView.Adapter<CommentAdapter.ViewHolder>() {

    interface CommentAdapterAdapterInterface {
        fun onReplyClick(adapterPosition: Int, category: CommentModel)
        fun onLikeOnClick(adapterPosition: Int, category: CommentModel)
        fun onLikeOffOnClick(adapterPosition: Int, category: CommentModel)
        fun onSendReplyClick(adapterPosition: Int, category: CommentModel, comment: String)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemCommentBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindView(commentLists[position])
    }

    override fun getItemCount(): Int {
        return commentLists.size
    }

    inner class ViewHolder(
        val binding: ItemCommentBinding,
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bindView(comment: CommentModel) {
            binding.tvCommentName.text = comment.commentName
            binding.tvComment.text = comment.comment
            if (comment.avatarUrl != "") {
                Glide
                    .with(context)
                    .load(comment.avatarUrl)
                    .override(300, 400)
                    .placeholder(R.drawable.book_cover_default)
                    .override(300, 400)
                    .into(binding.ivCommentAvatar)
            }
            binding.tvLikeNumber.text = comment.likeNumber.toString()
            if (comment.likeState) {
                binding.ivLikeOn.visibility = View.VISIBLE
                binding.ivLikeOff.visibility = View.GONE
            } else {
                binding.ivLikeOff.visibility = View.VISIBLE
                binding.ivLikeOn.visibility = View.GONE
            }

            if (comment.reply.isNotEmpty()) {
                binding.rcvReply.adapter =
                    CommentAdapter(context, comment.reply, commentAdapterAdapterInterface)
                binding.rcvReply.layoutManager =
                    LinearLayoutManager(context, RecyclerView.VERTICAL, false)
            }

            binding.ivLikeOn.setOnClickListener {
                commentAdapterAdapterInterface.onLikeOnClick(adapterPosition, comment)
                binding.ivLikeOn.visibility = View.GONE
                binding.ivLikeOff.visibility = View.VISIBLE
            }

            binding.ivLikeOff.setOnClickListener {
                commentAdapterAdapterInterface.onLikeOffOnClick(adapterPosition, comment)
                binding.ivLikeOff.visibility = View.GONE
                binding.ivLikeOn.visibility = View.VISIBLE
            }

            binding.tvReplyLabel.setOnClickListener {
                commentAdapterAdapterInterface.onReplyClick(adapterPosition, comment)
                if (binding.llReply.visibility == View.GONE) {
                    binding.llReply.visibility = View.VISIBLE
                    binding.rcvReply.visibility = View.VISIBLE
                } else {
                    binding.llReply.visibility = View.GONE
                    binding.rcvReply.visibility = View.GONE
                }
            }

            binding.ivSendReply.setOnClickListener {
                if (binding.edtReply.text != null) {
                    commentAdapterAdapterInterface.onSendReplyClick(
                        adapterPosition,
                        comment,
                        binding.edtReply.text.toString()
                    )
                }
                binding.edtReply.setText("")
                binding.llReply.visibility = View.GONE
            }
        }
    }
}
