package com.kiluss.bookrate.adapter

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.view.LayoutInflater
import android.view.ViewGroup
import androidx.recyclerview.widget.RecyclerView
import com.kiluss.bookrate.data.model.BookModel
import com.kiluss.bookrate.data.model.Tags
import com.kiluss.bookrate.databinding.ItemBookPreviewBinding

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
            binding.tvTitleBookPreview.text = bookModel.name
            //binding.tvBookState.text = bookModel.state
            if (bookModel.picture != null) {
                binding.ivBookPreview.setImageBitmap(base64ToBitmapDecode(bookModel.picture.toString()))
            }
            binding.tvAuthor.text = bookModel.author?.name.toString()
            binding.tvGenre.text = displayCategoryString(bookModel.tags)
            binding.tvPublishTime.text = bookModel.publishedYear.toString()
            binding.root.setOnClickListener {
                bookPreviewAdapterInterface.onItemViewClick(adapterPosition)
            }
            binding.llBookState.setOnClickListener{
                bookPreviewAdapterInterface.onBookStateClick(adapterPosition, binding.llBookState)
            }
        }

        private fun base64ToBitmapDecode(base64Image: String): Bitmap? {
            val decodedString = Base64.decode(base64Image, Base64.DEFAULT)
            return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
        }

        private fun displayCategoryString(tags: ArrayList<Tags>?): String {
            val listTagName = arrayListOf<String>()
            if (tags != null) {
                for (tag in tags) {
                    tag.tag?.name?.let { listTagName.add(it) }
                }
                return listTagName.toString().replace("[", "").replace("]", "")
            } else return "No category"
        }
    }
}
