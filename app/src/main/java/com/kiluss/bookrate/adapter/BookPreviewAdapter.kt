package com.kiluss.bookrate.adapter

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.RecyclerView
import com.kiluss.bookrate.data.model.BookModel
import com.kiluss.bookrate.data.model.BookRate
import com.kiluss.bookrate.data.model.Tags
import com.kiluss.bookrate.databinding.ItemBookPreviewBinding
import com.kiluss.bookrate.network.api.BookService
import com.kiluss.bookrate.network.api.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.lang.Exception

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
        val binding: ItemBookPreviewBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bindView(bookModel: BookModel) {
            binding.tvTitleBookPreview.text = bookModel.name
            //binding.tvBookState.text = bookModel.state
            if (bookModel.picture != null && bookModel.picture != "" && bookModel.picture != "null") {
                try {
                    binding.ivBookPreview.setImageBitmap(base64ToBitmapDecode(bookModel.picture.toString()))
                } catch (ex: Exception) {
                    ex.printStackTrace()
                }
            }
            if (bookModel.author != null) {
                binding.tvAuthor.text = bookModel.author!!.stageName.toString()
            } else {
                binding.llAuthor.visibility = View.GONE
            }
            if (!bookModel.tags.isNullOrEmpty()) {
                if (bookModel.tags?.get(0) != null) {
                    binding.tvGenre.text = displayCategoryString(bookModel.tags)
                } else {
                    binding.llCategory.visibility = View.GONE
                }
            } else {
                binding.llCategory.visibility = View.GONE
            }
            getAndUpdateRate(bookModel.id, context)
            binding.tvPublishTime.text = bookModel.publishedYear.toString()
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

        private fun displayCategoryString(tags: ArrayList<Tags>?): String {
            val listTagName = arrayListOf<String>()
            return if (tags != null) {
                for (tag in tags) {
                    tag.tag?.name?.let { listTagName.add(it) }
                }
                listTagName.toString().replace("[", "").replace("]", "")
            } else "No category"
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
