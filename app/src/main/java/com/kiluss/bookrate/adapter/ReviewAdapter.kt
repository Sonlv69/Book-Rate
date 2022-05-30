package com.kiluss.bookrate.adapter

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kiluss.bookrate.data.model.Reviews
import com.kiluss.bookrate.databinding.ItemReviewBinding

class ReviewAdapter(
    private val context: Context,
    private val reviewLists: List<Reviews>,
    private val commentAdapterAdapterInterface: CommentAdapterAdapterInterface
) : RecyclerView.Adapter<ReviewAdapter.ViewHolder>() {

    interface CommentAdapterAdapterInterface {
        fun onReplyClick(adapterPosition: Int, category: Reviews)
        fun onLikeOnClick(adapterPosition: Int, category: Reviews)
        fun onLikeOffOnClick(adapterPosition: Int, category: Reviews)
        fun onSendReplyClick(adapterPosition: Int, category: Reviews, comment: String)
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemReviewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindView(reviewLists[position])
    }

    override fun getItemCount(): Int {
        return reviewLists.size
    }

    inner class ViewHolder(
        val binding: ItemReviewBinding,
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bindView(review: Reviews) {
            binding.tvCommentName.text = review.iDAcc.toString()
            binding.tvComment.text = review.content.toString()
//            if (review.picture != "") {
//
//            }

            binding.tvDate.text = review.date.toString().split("T")[0]
            binding.rbReview.rating = 3.5F

            if (review.reviewChildrens?.isNotEmpty() == true) {
                binding.tvShowReply.setOnClickListener {
                    if (binding.rcvReply.visibility == View.GONE) {
                        binding.rcvReply.visibility = View.VISIBLE
                        binding.tvShowReply.text = "Hide reply"
                    } else {
                        binding.rcvReply.visibility = View.GONE
                        binding.tvShowReply.text = "Show reply"
                    }
                }
                binding.rcvReply.adapter =
                    ReplyReviewAdapter(context, review.reviewChildrens!!)
                binding.rcvReply.layoutManager =
                    LinearLayoutManager(context, RecyclerView.VERTICAL, false)
            } else {
                binding.tvShowReply.visibility = View.GONE
            }


            binding.tvReplyLabel.setOnClickListener {
                commentAdapterAdapterInterface.onReplyClick(adapterPosition, review)
                if (binding.llReply.visibility == View.GONE) {
                    binding.llReply.visibility = View.VISIBLE
                    binding.rcvReply.visibility = View.VISIBLE
                    binding.tvShowReply.text = "Hide reply"
                } else {
                    binding.llReply.visibility = View.GONE
                }
            }

            binding.ivSendReply.setOnClickListener {
                if (binding.edtReply.text != null) {
                    commentAdapterAdapterInterface.onSendReplyClick(
                        adapterPosition,
                        review,
                        binding.edtReply.text.toString()
                    )
                }
                binding.edtReply.setText("")
                binding.llReply.visibility = View.GONE
            }
        }
        private fun base64ToBitmapDecode(base64Image: String): Bitmap? {
            val decodedString = Base64.decode(base64Image, Base64.DEFAULT)
            return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
        }
    }
}
