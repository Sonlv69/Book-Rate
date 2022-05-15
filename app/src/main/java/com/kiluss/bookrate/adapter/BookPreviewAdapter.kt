package com.kiluss.bookrate.adapter

import android.content.Context
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.bumptech.glide.Glide
import com.kiluss.bookrate.databinding.ItemBookPreviewBinding
import com.kiluss.model.BookModel

class BookPreviewAdapter(
    private val bookLists: List<BookModel>,
    private val context: Context,
    private val bookPreviewAdapterInterface: BookPreviewAdapterInterface
) : RecyclerView.Adapter<BookPreviewAdapter.BookPreviewHolder>() {

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): BookPreviewHolder {
        val binding =
            ItemBookPreviewBinding.inflate(LayoutInflater.from(parent.context), parent, false)
        return BookPreviewHolder(binding)
    }

    override fun onBindViewHolder(holder: BookPreviewHolder, position: Int) {
        holder.bindView(bookLists[position])
    }

    override fun getItemCount(): Int {
        return bookLists.size
    }

    inner class BookPreviewHolder(
        val binding: ItemBookPreviewBinding,
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bindView(bookModel: BookModel) {
            binding.tvTitleBookPreview.text = bookModel.bookTitle
            binding.tvBookState.text = bookModel.bookState
            Glide
                .with(context)
                .load("https://cdn.animenewsnetwork.com/thumbnails/fit600x1000/cms/feature/132523/hello.jpg")
                .override(300, 400)
                .into(binding.ivBookPreview)
            binding.root.setOnClickListener {
                bookPreviewAdapterInterface.onItemViewClick(adapterPosition)
            }
            binding.llBookState.setOnClickListener{
                bookPreviewAdapterInterface.onBookStateClick(adapterPosition, binding.llBookState)
            }
        }
    }
}
