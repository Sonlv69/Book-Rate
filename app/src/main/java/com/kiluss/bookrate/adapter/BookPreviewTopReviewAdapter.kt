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
import com.kiluss.bookrate.data.model.Tags
import com.kiluss.bookrate.data.model.TopReview
import com.kiluss.bookrate.databinding.ItemBookPreviewBinding
import com.kiluss.bookrate.network.api.BookService
import com.kiluss.bookrate.network.api.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception

class BookPreviewTopReviewAdapter(
    private val bookLists: List<TopReview>,
    private val context: Context,
    private val bookPreviewAdapterInterface: BookPreviewAdapterInterface
) : RecyclerView.Adapter<BookPreviewTopReviewAdapter.BookPreviewHolder>() {

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
        val binding: ItemBookPreviewBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bindView(bookModel: TopReview) {
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
            binding.tvPublishTime.text = "${bookModel.reviewCount} reviews"
            getAndUpdateRate(bookModel.iDBook, context)
            binding.root.setOnClickListener {
                bookPreviewAdapterInterface.onItemViewClick(adapterPosition)
            }
            binding.llBookState.setOnClickListener{
                bookPreviewAdapterInterface.onBookStateClick(adapterPosition, binding.llBookState, 0)
            }
        }

        private fun base64ToBitmapDecode(base64Image: String): Bitmap? {
            val decodedString = Base64.decode(base64Image, Base64.DEFAULT)
            return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
        }

        private fun getAndUpdateRate(id: Int?, context: Context) {
            id?.let {
                RetrofitClient.getInstance(context).getClientUnAuthorize()
                    .create(BookService::class.java).getBookRate(it).enqueue(object : Callback<BookRate?> {
                    override fun onResponse(call: Call<BookRate?>, response: Response<BookRate?>) {
                        when {
                            response.code() == 404 -> {
                                Toast.makeText(
                                    context,
                                    "Url is not exist",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                            response.code() == 500 -> {
                                Toast.makeText(
                                    context,
                                    "Internal error",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                            response.isSuccessful -> {
                                if (response.body()?.rateAvg != null) {
                                    binding.rbRating.rating = response.body()!!.rateAvg!!.toFloat()
                                } else {
                                    binding.rbRating.visibility = View.GONE
                                }
                            }
                        }
                    }

                    override fun onFailure(call: Call<BookRate?>, t: Throwable) {
                        Toast.makeText(
                            context,
                            t.message,
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                })
            }
        }
    }
}
