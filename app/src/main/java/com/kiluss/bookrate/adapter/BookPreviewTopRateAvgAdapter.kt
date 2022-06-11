package com.kiluss.bookrate.adapter

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.kiluss.bookrate.data.model.BookRate
import com.kiluss.bookrate.data.model.TopRateAvg
import com.kiluss.bookrate.databinding.ItemBookPreviewBinding
import com.kiluss.bookrate.network.api.BookService
import com.kiluss.bookrate.network.api.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception
import java.util.*
import kotlin.collections.ArrayList

class BookPreviewTopRateAvgAdapter(
    private val bookLists: ArrayList<TopRateAvg>,
    private val context: Context,
    private val bookPreviewAdapterInterface: BookPreviewAdapterInterface
) : RecyclerView.Adapter<BookPreviewTopRateAvgAdapter.BookPreviewHolder>() {

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

    internal fun setData(list: ArrayList<TopRateAvg>) {
        bookLists.clear()
        bookLists.addAll(list)
        notifyDataSetChanged()
    }

    inner class BookPreviewHolder(
        val binding: ItemBookPreviewBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bindView(bookModel: TopRateAvg) {
            binding.tvTitleBookPreview.text = bookModel.name
            if (bookModel.picture != null && bookModel.picture != "" && bookModel.picture != "null") {
                try {
                    binding.ivBookPreview.setImageBitmap(base64ToBitmapDecode(bookModel.picture.toString()))
                } catch (ex: Exception) {
                    ex.printStackTrace()
                }
            }
            binding.apply {
                llBookState.visibility = View.GONE
                llAuthor.visibility = View.GONE
                llCategory.visibility = View.GONE
                ivPublishTime.visibility = View.GONE
            }
            binding.tvPublishTime.text = "Rate avg: ${String.format("%.1f", bookModel.rateAvg)}"
            binding.rbRating.rating = bookModel.rateAvg?.toFloat()!!
            binding.root.setOnClickListener {
                bookPreviewAdapterInterface.onItemViewClick(adapterPosition)
            }
            binding.llBookState.setOnClickListener {
                bookPreviewAdapterInterface.onBookStateClick(
                    adapterPosition,
                    binding.llBookState,
                    0
                )
            }
        }

        private fun base64ToBitmapDecode(base64Image: String): Bitmap? {
            val decodedString = Base64.decode(base64Image, Base64.DEFAULT)
            return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
        }
    }
}
