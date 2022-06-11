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
import com.kiluss.bookrate.data.model.BookModel
import com.kiluss.bookrate.data.model.BookRate
import com.kiluss.bookrate.data.model.MyBookState
import com.kiluss.bookrate.data.model.Tags
import com.kiluss.bookrate.databinding.ItemBookPreviewBinding
import com.kiluss.bookrate.network.api.BookService
import com.kiluss.bookrate.network.api.RetrofitClient
import com.kiluss.bookrate.utils.Constants
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MyBookPreviewAdapter(
    private val bookLists: MutableList<MyBookState>,
    private val context: Context,
    private val bookPreviewAdapterInterface: BookPreviewAdapterInterface
) : RecyclerView.Adapter<MyBookPreviewAdapter.BookPreviewHolder>() {

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

    internal fun changeData(books: MutableList<MyBookState>) {
        bookLists.clear()
        bookLists.addAll(books)
        notifyDataSetChanged()
    }

    inner class BookPreviewHolder(
        val binding: ItemBookPreviewBinding
    ) : RecyclerView.ViewHolder(binding.root) {

        fun bindView(bookModel: MyBookState) {
            showBookState(bookModel)
            binding.llBookState.visibility = View.VISIBLE
            bookModel.book?.apply {
                binding.tvTitleBookPreview.text = name
                if (picture != null && picture != "" && picture != "null") {
                    try {
                        binding.ivBookPreview.setImageBitmap(base64ToBitmapDecode(picture.toString()))
                    } catch (ex: Exception) {
                        ex.printStackTrace()
                    }
                }
                if (author != null) {
                    binding.tvAuthor.text = author!!.name.toString()
                } else {
                    binding.llAuthor.visibility = View.INVISIBLE
                }
                if (!tags.isNullOrEmpty()) {
                    if (tags?.get(0) != null) {
                        binding.llCategory.visibility = View.VISIBLE
                        binding.tvGenre.text = displayCategoryString(tags)
                    } else {
                        binding.llCategory.visibility = View.INVISIBLE
                    }
                } else {
                    binding.llCategory.visibility = View.INVISIBLE
                }
                getAndUpdateRate(id, context)
                binding.tvPublishTime.text = publishedYear.toString()
                binding.root.setOnClickListener {
                    bookPreviewAdapterInterface.onItemViewClick(adapterPosition)
                }
                binding.llBookState.setOnClickListener{
                    bookModel.statusBook?.let { it1 ->
                        bookPreviewAdapterInterface.onBookStateClick(adapterPosition, binding.llBookState,
                            it1
                        )
                    }
                }
            }
        }

        private fun showBookState(book: MyBookState) {
            book.apply {
                binding.apply {
                    when (statusBook) {
                        1 -> {
                            tvBookState.text = Constants.WANT_TO_READ
                        }
                        2 -> {
                            tvBookState.text = Constants.CURRENTLY_READING
                        }
                        3 -> {
                            tvBookState.text = Constants.READ
                        }
                        else -> {
                            tvBookState.text = Constants.UN_READ
                        }
                    }
                }
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
