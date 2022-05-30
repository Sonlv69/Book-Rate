package com.kiluss.bookrate.activity

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.widget.Toast
import androidx.activity.viewModels
import androidx.appcompat.app.AppCompatActivity
import androidx.core.app.ShareCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.kiluss.bookrate.R
import com.kiluss.bookrate.adapter.ReviewAdapter
import com.kiluss.bookrate.data.model.*
import com.kiluss.bookrate.utils.Const.Companion.EXTRA_MESSAGE
import com.kiluss.bookrate.databinding.ActivityBookDetailBinding
import com.kiluss.bookrate.network.api.BookService
import com.kiluss.bookrate.network.api.RetrofitClient
import com.kiluss.bookrate.utils.Const.Companion.API_URL
import com.kiluss.bookrate.viewmodel.MainActivityViewModel
import okhttp3.RequestBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class BookDetailActivity : AppCompatActivity(), ReviewAdapter.CommentAdapterAdapterInterface {
    private lateinit var loginResponse: LoginResponse
    private lateinit var binding: ActivityBookDetailBinding
    private lateinit var reviewAdapter: ReviewAdapter
    private lateinit var listReviews: List<Reviews>
    private lateinit var apiUnauthorized: BookService
    private lateinit var apiAuthorized: BookService
    private lateinit var book: BookModel
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBookDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        listReviews = arrayListOf()

        val bookId = intent.getIntExtra(EXTRA_MESSAGE, 0)

        //binding.tvReviewNumber.text = book?.reviews?.size.toString()
        binding.detailsBackButton.setOnClickListener {
            this.finish()
        }
        loginResponse = getLoginResponse(this)
        apiUnauthorized = RetrofitClient.getInstance(this).getClientUnAuthorize()
            .create(BookService::class.java)
        apiAuthorized = RetrofitClient.getInstance(this).getClientAuthorized(loginResponse.token!!)
            .create(BookService::class.java)
        getBookById(bookId.toString())
    }

    private fun getBookById(bookId: String) {
        apiUnauthorized.getBookById(bookId).enqueue(object : Callback<BookModel?> {
            override fun onResponse(call: Call<BookModel?>, response: Response<BookModel?>) {
                when {
                    response.code() == 404 -> {
                        Toast.makeText(
                            applicationContext,
                            "Url is not exist",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    response.code() == 500 -> {
                        Toast.makeText(
                            applicationContext,
                            "Internal error",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    response.isSuccessful -> {
                        response.body()?.let {
                            book = response.body()!!
                            setUpBookUi()
                        }
                    }
                }
            }

            override fun onFailure(call: Call<BookModel?>, t: Throwable) {
                Toast.makeText(
                    applicationContext,
                    t.message,
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

    private fun setUpBookUi() {
        getAndUpdateRate()
        binding.ivShare.setOnClickListener {
            ShareCompat.IntentBuilder(this)
                .setType("text/plain")
                .setChooserTitle("Share ${book.name}:")
                .setText("$API_URL/book/${book.id}")
                .startChooser();
        }
        binding.tvBookTitle.text = book.name
        supportActionBar?.title = book.name
        if (book.picture != null) {
            binding.ivCover.setImageBitmap(base64ToBitmapDecode(book.picture.toString()))
        }
        binding.tvAuthor.text = getString(R.string.by_author_text, book.author?.name.toString())
        binding.tvPublisher.text =
            getString(R.string.publish_by_text, book.publisher?.name.toString())
        binding.tvCategory.text = displayCategoryString(book.tags)
        binding.tvReviewed.text = book.reviews?.size.toString()
        if (book.description.toString() != "") {
            binding.tvDescription.text = book.description.toString()
        }
        binding.tvAuthor.setOnClickListener {
            val intent = Intent(this, AuthorProfileActivity::class.java).apply {
                putExtra(EXTRA_MESSAGE, book.iDAut)
                addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
            }
            startActivity(intent)
        }
        binding.tvPublisher.setOnClickListener {
            val intent = Intent(this, PublisherProfileActivity::class.java).apply {
                putExtra(EXTRA_MESSAGE, book.iDPub)
                addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
            }
            startActivity(intent)
        }
        book.reviews?.let {
            listReviews = book.reviews!!
            reviewAdapter = ReviewAdapter(this, listReviews, this)
            binding.rcvComment.adapter = reviewAdapter
            binding.rcvComment.layoutManager =
                LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        }
        binding.btnSendRate.setOnClickListener {
            apiAuthorized.createOrUpdateRate(
                createRequestBody(
                    book.id!!,
                    binding.rbRating.rating.toInt()
                )
            ).enqueue(object : Callback<Any?> {
                override fun onResponse(call: Call<Any?>, response: Response<Any?>) {
                    when {
                        response.code() == 404 -> {
                            Toast.makeText(
                                applicationContext,
                                "Url is not exist",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        response.code() == 500 -> {
                            Toast.makeText(
                                applicationContext,
                                "Internal error",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        response.isSuccessful -> {
                            Toast.makeText(
                                applicationContext,
                                "Rated!",
                                Toast.LENGTH_SHORT
                            ).show()
                            getBookById(book.id.toString())
                        }
                    }
                }

                override fun onFailure(call: Call<Any?>, t: Throwable) {
                    Toast.makeText(
                        applicationContext,
                        t.message,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
        }
    }

    private fun createRequestBody(idBook: Int, rate: Int) = run {
        val json = JSONObject()
        json.put("ID_Acc", loginResponse.id)
        json.put("ID_Book", idBook)
        json.put("Rate", rate)
        RequestBody.create(
            okhttp3.MediaType.parse("application/json; charset=utf-8"),
            json.toString()
        )
    }

    private fun getAndUpdateRate() {
        book.id?.let {
            apiUnauthorized.getBookRate(it).enqueue(object : Callback<BookRate?> {
                override fun onResponse(call: Call<BookRate?>, response: Response<BookRate?>) {
                    when {
                        response.code() == 404 -> {
                            Toast.makeText(
                                applicationContext,
                                "Url is not exist",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        response.code() == 500 -> {
                            Toast.makeText(
                                applicationContext,
                                "Internal error",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        response.isSuccessful -> {
                            binding.tvRating.text = String.format("%,.1f", response.body()?.rateAvg)
                        }
                    }
                }

                override fun onFailure(call: Call<BookRate?>, t: Throwable) {
                    Toast.makeText(
                        applicationContext,
                        t.message,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
        }
    }

    private fun displayCategoryString(tags: ArrayList<Tags>?): String {
        var listTagName = arrayListOf<String>()
        if (tags != null) {
            for (tag in tags) {
                tag.tag?.name?.let { listTagName.add(it) }
            }
            return listTagName.toString().replace("[", "").replace("]", "")
        } else return "No category"
    }

    private fun base64ToBitmapDecode(base64Image: String): Bitmap? {
        val decodedString = Base64.decode(base64Image, Base64.DEFAULT)
        return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
    }

    override fun onReplyClick(adapterPosition: Int, category: Reviews) {

    }

    override fun onLikeOnClick(adapterPosition: Int, category: Reviews) {

    }

    override fun onLikeOffOnClick(adapterPosition: Int, category: Reviews) {

    }

    override fun onSendReplyClick(adapterPosition: Int, category: Reviews, comment: String) {

    }

    private fun getLoginResponse(context: Context): LoginResponse {
        val sharedPref = context.getSharedPreferences(
            context.getString(R.string.saved_login_account_key),
            Context.MODE_PRIVATE
        )
        val gson = Gson()
        val json: String? =
            sharedPref.getString(context.getString(R.string.saved_login_account_key), "")
        return gson.fromJson(json, LoginResponse::class.java)
    }
}
