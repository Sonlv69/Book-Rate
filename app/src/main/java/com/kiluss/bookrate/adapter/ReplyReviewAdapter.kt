package com.kiluss.bookrate.adapter

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.app.AlertDialog
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.RecyclerView
import com.kiluss.bookrate.data.model.ReviewChildrens
import com.kiluss.bookrate.databinding.ItemReplyReviewBinding

class ReplyReviewAdapter(
    private val context: Context,
    private val reviewLists: List<ReviewChildrens>,
    private val id: Int,
    private val replyAdapterAdapterInterface: ReplyAdapterAdapterInterface
) : RecyclerView.Adapter<ReplyReviewAdapter.ViewHolder>() {

    interface ReplyAdapterAdapterInterface {
        fun onEditReply(id: Int, idParent: Int, currentContent: String)
        fun onDeleteReply(id: Int, idParent: Int)
        fun onToAccountInfoPage(accountId: Int)
    }

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
            binding.apply {
                tvCommentName.text = reply.account?.userName.toString()
                tvComment.text = reply.content.toString()
                if (reply.account?.picture.toString() != "") {
                    ivCommentAvatar.setImageBitmap(base64ToBitmapDecode(reply.account?.picture.toString()))
                }
                if (id == reply.iDAcc) {
                    ivMore.visibility = View.VISIBLE
                }
                ivMore.setOnClickListener {
                    showOverflowMenu(reply, ivMore)
                }
                tvDate.text = reply.date.toString().split("T")[0]
                ivCommentAvatar.setOnClickListener {
                    reply.iDAcc?.let { it1 -> replyAdapterAdapterInterface.onToAccountInfoPage(it1) }
                }
                tvCommentName.setOnClickListener {
                    reply.iDAcc?.let { it1 -> replyAdapterAdapterInterface.onToAccountInfoPage(it1) }
                }
            }
        }

        private fun showOverflowMenu(reply: ReviewChildrens, anchor: View) {
            val menu = PopupMenu(context, anchor)
            menu.menu.apply {
                add("Edit reply").setOnMenuItemClickListener {
                    reply.id?.let { it1 ->
                        reply.idParent?.let { it2 ->
                            replyAdapterAdapterInterface.onEditReply(
                                it1,
                                it2, reply.content.toString()
                            )
                        }
                    }
                    true
                }
                add("Delete reply").setOnMenuItemClickListener {
                    reply.id?.let { it1 ->
                        reply.idParent?.let { it2 ->
                            createDeleteDialog(
                                it1,
                                it2
                            )
                        }
                    }
                    true
                }
            }
            menu.show()
        }

        private fun base64ToBitmapDecode(base64Image: String): Bitmap? {
            val decodedString = Base64.decode(base64Image, Base64.DEFAULT)
            return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
        }

        private fun createDeleteDialog(id: Int, idParent: Int) {
            val builder = AlertDialog.Builder(context)
            builder.setTitle("Delete")
            builder.setMessage("Are you sure to delete")
            builder.setPositiveButton(android.R.string.ok) { _, _ ->
                deleteReview(id, idParent)
            }
            builder.setNegativeButton(android.R.string.cancel) { _, _ ->
            }
            builder.show()
        }

        private fun deleteReview(id: Int, idParent: Int) {
            replyAdapterAdapterInterface.onDeleteReply(id, idParent)
        }
    }
}
