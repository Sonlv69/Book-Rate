package com.kiluss.bookrate.activity

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.graphics.Typeface
import android.os.Bundle
import android.text.SpannableString
import android.text.style.StyleSpan
import android.util.Base64
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.appcompat.widget.PopupMenu
import androidx.core.app.ShareCompat
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.kiluss.bookrate.R
import com.kiluss.bookrate.adapter.ReviewAdapter
import com.kiluss.bookrate.data.model.*
import com.kiluss.bookrate.databinding.ActivityBookDetailBinding
import com.kiluss.bookrate.fragment.CategoryDialogFragment
import com.kiluss.bookrate.network.api.BookService
import com.kiluss.bookrate.network.api.RetrofitClient
import com.kiluss.bookrate.utils.Constants.Companion.API_URL
import com.kiluss.bookrate.utils.Constants.Companion.CURRENTLY_READING
import com.kiluss.bookrate.utils.Constants.Companion.EXTRA_MESSAGE
import com.kiluss.bookrate.utils.Constants.Companion.READ
import com.kiluss.bookrate.utils.Constants.Companion.UN_READ
import com.kiluss.bookrate.utils.Constants.Companion.WANT_TO_READ
import okhttp3.RequestBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class BookDetailActivity : AppCompatActivity(), ReviewAdapter.CommentAdapterAdapterInterface {
    private var bookId: Int = 0
    private lateinit var loginResponse: LoginResponse
    private lateinit var binding: ActivityBookDetailBinding
    private lateinit var reviewAdapter: ReviewAdapter
    private lateinit var listReviews: List<Reviews>
    private lateinit var apiUnauthorized: BookService
    private lateinit var apiAuthorized: BookService
    private lateinit var book: BookModel
    private var bookState = 0
    private var idReview = -1
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityBookDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        listReviews = arrayListOf()
        bookId = intent.getIntExtra(EXTRA_MESSAGE, 0)
        //binding.tvReviewNumber.text = book?.reviews?.size.toString()
        binding.detailsBackButton.setOnClickListener {
            this.finish()
        }
        loginResponse = getLoginResponse(this)
        apiUnauthorized = RetrofitClient.getInstance(this).getClientUnAuthorize()
            .create(BookService::class.java)
        apiAuthorized = RetrofitClient.getInstance(this).getClientAuthorized(loginResponse.token!!)
            .create(BookService::class.java)
        getBookById(bookId.toString(), false, idReview)
        getMyBook()
    }

    override fun onNewIntent(intent: Intent?) {
        super.onNewIntent(intent)
        bookId = intent?.getIntExtra(EXTRA_MESSAGE, 0)!!
        getBookById(bookId.toString(), false, idReview)
    }

    private fun getBookById(bookId: String, isShowReply: Boolean, idReview: Int) {
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
                            Log.e("response", response.body()!!.toString())
                            book = response.body()!!
                            setUpBookUi(isShowReply, idReview)
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

    private fun setUpBookUi(isShowReply: Boolean, idReview: Int) {
        getAndUpdateRate()
        binding.ivShare.setOnClickListener {
            ShareCompat.IntentBuilder(this)
                .setType("text/plain")
                .setChooserTitle("Share ${book.name}:")
                .setText("$API_URL/book/${book.id}")
                .startChooser()
        }
        binding.tvBookTitle.text = book.name
        supportActionBar?.title = book.name
        if (book.picture != null && book.picture != "" && book.picture != "null") {
            binding.ivCover.setImageBitmap(base64ToBitmapDecode(book.picture.toString()))
        }
        val authorString =
            SpannableString(getString(R.string.by_author_text, book.author?.name.toString()))
        authorString.setSpan(StyleSpan(Typeface.ITALIC), 0, authorString.length, 0)
        binding.tvAuthor.text = authorString

        binding.tvPublisher.text =
            getString(R.string.publish_by_text, book.publisher?.name.toString())
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
        binding.tvCategory.text = displayCategoryString(book.tags)
        binding.tvCategory.setOnClickListener {
            if (book.tags?.size == 1) {
                val intent = Intent(this, CategoryDetailActivity::class.java).apply {
                    putExtra(EXTRA_MESSAGE, book.tags!![0].iDTag)
                }
                startActivity(intent)
            } else {
                book.tags?.let { it1 ->
                    CategoryDialogFragment.newInstance(it1).show(supportFragmentManager, "Category")
                }
            }
        }
        book.publishedYear?.let { binding.tvPublishYear.text = it.toString() }
        book.reviews?.let {
            listReviews = book.reviews!!
            reviewAdapter = ReviewAdapter(this, listReviews, this, isShowReply, idReview)
            binding.rcvComment.adapter = reviewAdapter
            binding.rcvComment.layoutManager =
                LinearLayoutManager(this, RecyclerView.VERTICAL, false)
        }
        binding.btnSendRate.setOnClickListener {
            apiAuthorized.createReview(
                createRequestBodyForNewReview(
                    book.id!!,
                    binding.rbRating.rating.toInt(),
                    binding.edtReview.text.toString()
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
                        response.code() == 400 -> {
                            putReview()
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
                            binding.edtReview.setText("")
                            getBookById(book.id.toString(), false, this@BookDetailActivity.idReview)
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
        binding.llBookState.setOnClickListener {
            showOverflowMenu(it)
        }
    }

    private fun putReview() {
        apiAuthorized.putReview(
            findMyReviewId(), createRequestBodyForPutReview(
                binding.rbRating.rating.toInt(),
                binding.edtReview.text.toString()
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
                            "Changed exist review!",
                            Toast.LENGTH_SHORT
                        ).show()
                        getBookById(book.id.toString(), false, idReview)
                    }
                }
                binding.edtReview.setText("")
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

    private fun findMyReviewId(): Int {
        for (review in listReviews) {
            if (review.iDAcc == loginResponse.id) {
                return review.id!!
            }
        }
        return -1
    }

    private fun createRequestBodyForNewReview(idBook: Int, rate: Int, content: String) = run {
        val json = JSONObject()
        json.put("ID_Book", idBook)
        json.put("Rate", rate)
        json.put("content", content)
        RequestBody.create(
            okhttp3.MediaType.parse("application/json; charset=utf-8"),
            json.toString()
        )
    }

    private fun createRequestBodyForPutReview(rate: Int, content: String) = run {
        val json = JSONObject()
        json.put("Rate", rate)
        json.put("content", content)
        RequestBody.create(
            okhttp3.MediaType.parse("application/json; charset=utf-8"),
            json.toString()
        )
    }

    private fun createRequestBodyForReply(idParent: Int, content: String) = run {
        val json = JSONObject()
        json.put("id_parent", idParent)
        json.put("content", content)
        RequestBody.create(
            okhttp3.MediaType.parse("application/json; charset=utf-8"),
            json.toString()
        )
    }

    private fun createRequestBodyForPutReply(rate: String, content: Int) = run {
        val json = JSONObject()
        json.put("content", rate)
        json.put("id_parent", content)
        RequestBody.create(
            okhttp3.MediaType.parse("application/json; charset=utf-8"),
            json.toString()
        )
    }

    private fun createRequestBodyForPostMyBook(id: Int) = run {
        val json = JSONObject()
        json.put("iD_Book", id)
        RequestBody.create(
            okhttp3.MediaType.parse("application/json; charset=utf-8"),
            json.toString()
        )
    }

    private fun createRequestBodyForPutMyBook(id: Int, status: Int) = run {
        val json = JSONObject()
        json.put("iD_Book", id)
        json.put("status", status)
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
                            response.body()?.rateAvg?.let { rate ->
                                binding.tvRating.text = String.format("%,.1f", rate)
                            }
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
        val listTagName = arrayListOf<String>()
        return if (tags != null) {
            for (tag in tags) {
                tag.tag?.name?.let { listTagName.add(it) }
            }
            listTagName.toString().replace("[", "").replace("]", "")
        } else "No category"
    }

    private fun base64ToBitmapDecode(base64Image: String): Bitmap? {
        val decodedString = Base64.decode(base64Image, Base64.DEFAULT)
        return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
    }

    override fun onSendReplyClick(idParent: Int, comment: String) {
        idReview = idParent
        apiAuthorized.postReply(createRequestBodyForReply(idParent, comment))
            .enqueue(object : Callback<Any?> {
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
                            getBookById(book.id.toString(), true, idReview)
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

    override fun onDeleteReview(id: Int) {
        apiAuthorized.deleteReview(id).enqueue(object : Callback<Unit> {
            override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
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
                        getBookById(bookId.toString(), false, idReview)
                    }
                }
            }

            override fun onFailure(call: Call<Unit>, t: Throwable) {
                Toast.makeText(
                    applicationContext,
                    t.message,
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

    override fun onEditReply(id: Int, idParent: Int, currentContent: String) {
        binding.apply {
            llEditReply.visibility = View.VISIBLE
            edtEditReply.setText(currentContent)
            btnEditReply.setOnClickListener {
                llEditReply.visibility = View.GONE
                apiAuthorized.putReply(
                    id, createRequestBodyForPutReply(
                        binding.edtEditReply.text.toString(), idParent
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
                                getBookById(book.id.toString(), true, idReview)
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
            btnEditReplyCancel.setOnClickListener {
                binding.llEditReply.visibility = View.GONE
            }
        }
    }

    private fun showOverflowMenu(anchor: View) {
        val menu = PopupMenu(this, anchor)
        menu.menu.apply {
            add(UN_READ).setOnMenuItemClickListener {
                binding.tvBookState.text = UN_READ
                changeBookState(0)
                true
            }
            add(WANT_TO_READ).setOnMenuItemClickListener {
                binding.tvBookState.text = WANT_TO_READ
                changeBookState(1)
                true
            }
            add(CURRENTLY_READING).setOnMenuItemClickListener {
                binding.tvBookState.text = CURRENTLY_READING
                changeBookState(2)
                true
            }
            add(READ).setOnMenuItemClickListener {
                binding.tvBookState.text = READ
                changeBookState(3)
                true
            }
        }
        menu.show()
    }

    private fun changeBookState(newState: Int) {
        if (bookState == newState) {
            return
        }
        if (bookState == 0) {
            postMyBook()
            if (newState == 1) {
                return
            }
        }
        when (newState) {
            0 -> {
                deleteFromMyBook()
            }
            1 -> {
                putBookState(1)
            }
            2 -> {
                putBookState(2)
            }
            else -> {
                putBookState(3)
            }
        }
    }

    private fun showBookState(myBooks: ArrayList<MyBookState>) {
        myBooks.forEach {
            if (it.book?.id == bookId) {
                binding.apply {
                    when (it.statusBook) {
                        1 -> {
                            bookState = 1
                            tvBookState.text = WANT_TO_READ
                        }
                        2 -> {
                            bookState = 2
                            tvBookState.text = CURRENTLY_READING
                        }
                        3 -> {
                            bookState = 3
                            tvBookState.text = READ
                        }
                        else -> {
                            bookState = 0
                            tvBookState.text = UN_READ
                        }
                    }
                }
            }
        }
    }

    private fun getMyBook() {
        apiAuthorized.getMyBook().enqueue(object : Callback<ArrayList<MyBookState>> {
            override fun onResponse(call: Call<ArrayList<MyBookState>>, response: Response<ArrayList<MyBookState>>) {
                when {
                    response.code() == 400 -> {
                        Toast.makeText(
                            applicationContext,
                            "Bad request",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
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
                            showBookState(it)
                        }
                    }
                }
            }

            override fun onFailure(call: Call<ArrayList<MyBookState>>, t: Throwable) {
                Toast.makeText(
                    applicationContext,
                    t.message,
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

    private fun postMyBook() {
        apiAuthorized.postMyBook(createRequestBodyForPostMyBook(bookId)).enqueue(object : Callback<MyBookState> {
            override fun onResponse(call: Call<MyBookState>, response: Response<MyBookState>) {
                when {
                    response.code() == 400 -> {
                        Toast.makeText(
                            applicationContext,
                            "Bad request",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
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
                        if (bookState == 0) {
                            Toast.makeText(
                                applicationContext,
                                "Added to my book",
                                Toast.LENGTH_SHORT
                            ).show()
                        } else {
                            Toast.makeText(
                                applicationContext,
                                "Change book state",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                    }
                }
            }

            override fun onFailure(call: Call<MyBookState>, t: Throwable) {
                Toast.makeText(
                    applicationContext,
                    t.message,
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

    private fun putBookState(state: Int) {
        apiAuthorized.putMyBook(createRequestBodyForPutMyBook(bookId, state)).enqueue(object : Callback<MyBookState> {
            override fun onResponse(call: Call<MyBookState>, response: Response<MyBookState>) {
                when {
                    response.code() == 400 -> {
                        Toast.makeText(
                            applicationContext,
                            "Bad request",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
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
                        if (bookState != 0) {
                            Toast.makeText(
                                applicationContext,
                                "Change book state",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        bookState = state
                    }
                }
            }

            override fun onFailure(call: Call<MyBookState>, t: Throwable) {
                Toast.makeText(
                    applicationContext,
                    t.message,
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

    private fun deleteFromMyBook() {
        apiAuthorized.deleteMyBookById(bookId).enqueue(object : Callback<Unit> {
            override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
                when {
                    response.code() == 400 -> {
                        Toast.makeText(
                            applicationContext,
                            "Bad request",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
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
                            "Remove from my book",
                            Toast.LENGTH_SHORT
                        ).show()
                        bookState = 0
                    }
                }
            }

            override fun onFailure(call: Call<Unit>, t: Throwable) {
                Toast.makeText(
                    applicationContext,
                    t.message,
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

    override fun onDeleteReply(id: Int, idParent: Int) {
        idReview = idParent
        apiAuthorized.deleteReply(id).enqueue(object : Callback<Unit> {
            override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
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
                        getBookById(bookId.toString(), true, idReview)
                    }
                }
            }

            override fun onFailure(call: Call<Unit>, t: Throwable) {
                Toast.makeText(
                    applicationContext,
                    t.message,
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

    override fun onToAccountInfoPage(accountId: Int) {
        val intent = Intent(this, UserDetailActivity::class.java)
        intent.putExtra(EXTRA_MESSAGE, accountId)
        startActivity(intent)
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
