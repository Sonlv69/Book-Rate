package com.kiluss.bookrate.adapter

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.kiluss.bookrate.R
import com.kiluss.bookrate.data.model.LoginResponse
import com.kiluss.bookrate.data.model.Reviews
import com.kiluss.bookrate.databinding.ItemReviewBinding

class ReviewAdapter(
    private val context: Context,
    private val reviewLists: List<Reviews>,
    private val commentAdapterAdapterInterface: CommentAdapterAdapterInterface,
    private val isShowReply: Boolean,
    private val idReview: Int
) : RecyclerView.Adapter<ReviewAdapter.ViewHolder>() {

    private var loginResponse: LoginResponse

    interface CommentAdapterAdapterInterface {
        fun onSendReplyClick(idParent: Int, comment: String)
        fun onDeleteReview(id: Int)
        fun onEditReply(id: Int, idParent: Int, currentContent: String)
        fun onDeleteReply(id: Int, idParent: Int)
    }

    init {
        loginResponse = getLoginResponse(context)
    }

    private fun getLoginResponse(context: Context) : LoginResponse {
        val sharedPref = context.getSharedPreferences(
            context.getString(R.string.saved_login_account_key),
            Context.MODE_PRIVATE
        )
        val gson = Gson()
        val json: String? = sharedPref.getString(context.getString(R.string.saved_login_account_key), "")
        return gson.fromJson(json, LoginResponse::class.java)
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
    ) : RecyclerView.ViewHolder(binding.root), ReplyReviewAdapter.ReplyAdapterAdapterInterface{

        fun bindView(review: Reviews) {
            binding.tvCommentName.text = review.account?.userName.toString()
            binding.tvComment.text = review.content.toString()
            if (review.account?.picture.toString() != "" && review.account?.picture != null) {
                binding.ivCommentAvatar.setImageBitmap(base64ToBitmapDecode(review.account?.picture.toString()))
            }
            binding.tvDate.text = review.date.toString().split("T")[0]
            review.rate?.let { binding.rbReview.rating = it.toFloat() }

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
                    loginResponse.id?.let {
                        ReplyReviewAdapter(context, review.reviewChildrens!!, it, this)
                    }
                binding.rcvReply.layoutManager =
                    LinearLayoutManager(context, RecyclerView.VERTICAL, false)
            } else {
                binding.tvShowReply.visibility = View.GONE
            }

            if (review.iDAcc == loginResponse.id) {
                binding.ivDelete.visibility = View.VISIBLE
            }

            binding.tvReplyLabel.setOnClickListener {
                if (binding.llReply.visibility == View.GONE) {
                    binding.llReply.visibility = View.VISIBLE
                    binding.rcvReply.visibility = View.VISIBLE
                    binding.tvShowReply.text = "Hide reply"
                } else {
                    binding.llReply.visibility = View.GONE
                }
            }

            if (isShowReply && idReview == review.id) {
                binding.rcvReply.visibility = View.VISIBLE
                binding.llReply.visibility = View.GONE
                binding.tvShowReply.text = "Hide reply"
            }

            binding.ivSendReply.setOnClickListener {
                if (binding.edtReply.text != null) {
                    review.id?.let { it1 ->
                        commentAdapterAdapterInterface.onSendReplyClick(
                            it1,
                            binding.edtReply.text.toString()
                        )
                    }
                }
                binding.edtReply.setText("")
                binding.llReply.visibility = View.GONE
            }
            binding.ivDelete.setOnClickListener { review.id?.let { it1 -> createDeleteDialog(it1) } }
        }
        private fun base64ToBitmapDecode(base64Image: String): Bitmap? {
            val decodedString = Base64.decode(base64Image, Base64.DEFAULT)
            return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
        }

        private fun createDeleteDialog(id: Int) {
            val builder = AlertDialog.Builder(context)
            builder.setTitle("Delete")
            builder.setMessage("Are you sure to delete")
            builder.setPositiveButton(android.R.string.ok) { _, _ ->
                deleteReview(id)
            }
            builder.setNegativeButton(android.R.string.cancel) { _, _ ->
            }
            builder.show()
        }

        private fun deleteReview(id: Int) {
            commentAdapterAdapterInterface.onDeleteReview(id)
        }

        override fun onEditReply(id: Int, idParent: Int, currentContent: String) {
            commentAdapterAdapterInterface.onEditReply(id, idParent, currentContent)
        }

        override fun onDeleteReply(id: Int, idParent: Int) {
            commentAdapterAdapterInterface.onDeleteReply(id, idParent)
        }
    }
}
