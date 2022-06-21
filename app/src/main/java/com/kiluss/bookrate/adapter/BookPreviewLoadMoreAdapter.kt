package com.kiluss.bookrate.adapter

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Handler
import android.os.Looper
import android.util.Base64
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.content.res.AppCompatResources
import androidx.recyclerview.widget.RecyclerView
import com.kiluss.bookrate.R
import com.kiluss.bookrate.data.model.BookModel
import com.kiluss.bookrate.data.model.BookRate
import com.kiluss.bookrate.data.model.Tags
import com.kiluss.bookrate.databinding.ItemBookPreviewBinding
import com.kiluss.bookrate.network.api.BookService
import com.kiluss.bookrate.network.api.RetrofitClient
import com.kiluss.bookrate.utils.Constants
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class BookPreviewLoadMoreAdapter(
    private val context: Context,
    private val bookPreviewAdapterInterface: BookPreviewAdapterInterface
) : RecyclerView.Adapter<RecyclerView.ViewHolder>() {
    private lateinit var bookLists: ArrayList<BookModel?>

    inner class ItemViewHolder(val binding: ItemBookPreviewBinding) :
        RecyclerView.ViewHolder(binding.root) {
        fun bindView(bookModel: BookModel) {
            binding.tvTitleBookPreview.text = bookModel.name
            //binding.tvBookState.text = bookModel.state
            if (bookModel.picture != null && bookModel.picture != "" && bookModel.picture != "null") {
                binding.ivBookPreview.setImageBitmap(base64ToBitmapDecode(bookModel.picture.toString()))
            } else {
                binding.ivBookPreview.setImageDrawable(AppCompatResources.getDrawable(context, R.drawable.cover_not_available))
            }
            if (bookModel.author != null) {
                binding.tvAuthor.text = bookModel.author!!.stageName.toString()
            } else {
                binding.llAuthor.visibility = View.INVISIBLE
            }
            if (!bookModel.tags.isNullOrEmpty()) {
                if (bookModel.tags?.get(0) != null) {
                    binding.tvGenre.text = displayCategoryString(bookModel.tags)
                } else {
                    binding.llCategory.visibility = View.INVISIBLE
                }
            } else {
                binding.llCategory.visibility = View.INVISIBLE
            }
            getAndUpdateRate(bookModel.id, context)
            binding.tvPublishTime.text = bookModel.publishedYear.toString()
            binding.root.setOnClickListener {
                bookPreviewAdapterInterface.onItemViewClick(adapterPosition)
            }
            binding.llBookState.setOnClickListener {
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
                    .create(BookService::class.java).getBookRate(it).enqueue(object :
                        Callback<BookRate?> {
                        override fun onResponse(
                            call: Call<BookRate?>,
                            response: Response<BookRate?>
                        ) {
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
                                        binding.rbRating.rating =
                                            response.body()!!.rateAvg!!.toFloat()
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

    inner class LoadingViewHolder(itemView: View) : RecyclerView.ViewHolder(itemView)

    fun addData(dataViews: ArrayList<BookModel?>) {
        if (!this::bookLists.isInitialized) {
            bookLists = dataViews
            notifyDataSetChanged()
        } else {
            val newFirstIndex = this.bookLists.size
            this.bookLists.addAll(dataViews)
            notifyItemRangeChanged(newFirstIndex, bookLists.size - 1)
        }
    }

    fun getItemAtPosition(position: Int): BookModel? {
        return bookLists[position]
    }

    fun addLoadingView() {
        if (this::bookLists.isInitialized) {
            //Add loading item
            Handler(Looper.getMainLooper()).post {
                bookLists.add(null)
                notifyItemInserted(bookLists.size - 1)
            }
        }
    }

    fun removeLoadingView() {
        if (this::bookLists.isInitialized) {
            //Remove loading item
            if (bookLists.size != 0) {
                bookLists.removeAt(bookLists.size - 1)
                notifyItemRemoved(bookLists.size)
            }
        }
    }

    override fun onCreateViewHolder(parent: ViewGroup, viewType: Int): RecyclerView.ViewHolder {
        return if (viewType == Constants.VIEW_TYPE_ITEM) {
            ItemViewHolder(
                ItemBookPreviewBinding.inflate(
                    LayoutInflater.from(parent.context),
                    parent,
                    false
                )
            )
        } else {
            val view =
                LayoutInflater.from(parent.context)
                    .inflate(R.layout.progress_loading, parent, false)
            LoadingViewHolder(view)
        }
    }

    override fun getItemCount(): Int {
        return if (this::bookLists.isInitialized) {
            bookLists.size
        } else {
            0
        }
    }

    override fun getItemViewType(position: Int): Int {
        return if (bookLists[position] == null) {
            Constants.VIEW_TYPE_LOADING
        } else {
            Constants.VIEW_TYPE_ITEM
        }
    }

    override fun onBindViewHolder(holder: RecyclerView.ViewHolder, position: Int) {
        if (holder.itemViewType == Constants.VIEW_TYPE_ITEM) {
            bookLists[position]?.let { (holder as ItemViewHolder).bindView(it) }
        }
    }
}
