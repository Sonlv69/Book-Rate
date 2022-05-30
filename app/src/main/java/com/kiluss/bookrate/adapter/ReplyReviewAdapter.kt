package com.kiluss.bookrate.adapter

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.kiluss.bookrate.data.model.ReviewChildrens
import com.kiluss.bookrate.databinding.ItemReplyReviewBinding

class ReplyReviewAdapter(
    private val context: Context,
    private val reviewLists: List<ReviewChildrens>,
) : RecyclerView.Adapter<ReplyReviewAdapter.ViewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): ViewHolder {
        val binding =
            ItemReplyReviewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return ViewHolder(binding)
    }

    override fun onBindViewHolder(holder: ViewHolder, position: Int) {
        holder.bindView(reviewLists[position])
    }

    override fun getItemCount(): Int {
        return reviewLists.size
    }

    inner class ViewHolder(
        val binding: ItemReplyReviewBinding,
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bindView(reply: ReviewChildrens) {
            binding.tvCommentName.text = reply.iDAcc.toString()
            binding.tvComment.text = reply.content.toString()
//            if (review.picture != "") {
//
//            }
            binding.tvDate.text = reply.date.toString().split("T")[0]
        }
        private fun base64ToBitmapDecode(base64Image: String): Bitmap? {
            val decodedString = Base64.decode(base64Image, Base64.DEFAULT)
            return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
        }
    }
}
