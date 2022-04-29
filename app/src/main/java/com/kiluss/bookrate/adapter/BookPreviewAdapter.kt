package com.kiluss.bookrate.adapter;

import android.content.Context
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.ImageView
import android.widget.TextView
import androidx.recyclerview.widget.RecyclerView;
import com.bumptech.glide.Glide
import com.kiluss.bookrate.R
import com.kiluss.model.BookModel

class BookPreviewAdapter(
    private val mBookLists: List<BookModel>,
    private val mContext: Context?,
    private val mBookPreviewAdapterInterface: BookPreviewAdapterInterface
) : RecyclerView.Adapter<BookPreviewAdapter.BookPreviewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookPreviewHolder {
        val view =
            LayoutInflater.from(parent.context).inflate(R.layout.item_book_preview, parent, false)
        return BookPreviewHolder(view, mBookPreviewAdapterInterface)
    }

    override fun onBindViewHolder(holder: BookPreviewHolder, position: Int) {
        holder.tvBookPreviewTitle.setText(mBookLists.get(position).bookTitle)
        Glide
            .with(mContext!!)
            .load("https://cdn.animenewsnetwork.com/thumbnails/fit600x1000/cms/feature/132523/hello.jpg")
            .override(300, 400)
            .into(holder.ivBookPreview)
    }

    override fun getItemCount(): Int {
        return mBookLists.size
    }

    class BookPreviewHolder(
        itemView: View,
        bookPreviewAdapterInterface: BookPreviewAdapterInterface
    ) : RecyclerView.ViewHolder(itemView) {
        val tvBookPreviewTitle: TextView = itemView.findViewById(R.id.tvTitleBookPreview)
        val ivBookPreview: ImageView = itemView.findViewById(R.id.ivBookPreview)

        init {
            itemView.setOnClickListener({
                bookPreviewAdapterInterface.onItemClick(adapterPosition)
            })
        }
    }
}
