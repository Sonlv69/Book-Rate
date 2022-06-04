package com.kiluss.bookrate.activity

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.GridLayoutManager
import com.google.gson.Gson
import com.kiluss.bookrate.R
import com.kiluss.bookrate.adapter.BookPreviewAdapter
import com.kiluss.bookrate.adapter.BookPreviewAdapterInterface
import com.kiluss.bookrate.data.model.BookModel
import com.kiluss.bookrate.data.model.LoginResponse
import com.kiluss.bookrate.data.model.Publisher
import com.kiluss.bookrate.databinding.ActivityPublisherProfileBinding
import com.kiluss.bookrate.network.api.BookService
import com.kiluss.bookrate.network.api.RetrofitClient
import com.kiluss.bookrate.utils.Constants
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class PublisherProfileActivity : AppCompatActivity(), BookPreviewAdapterInterface {
    private lateinit var binding: ActivityPublisherProfileBinding
    private lateinit var api: BookService
    private lateinit var publisher: Publisher
    private lateinit var bookLists: ArrayList<BookModel>
    private lateinit var bookAdapter: BookPreviewAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPublisherProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val idPublisher = intent.getIntExtra(Constants.EXTRA_MESSAGE, 0)
        val loginResponse = getLoginResponse(this)
        api = RetrofitClient.getInstance(this).getClientAuthorized(loginResponse.token.toString())
            .create(BookService::class.java)
        getPublisherInfo(idPublisher)
    }

    private fun getPublisherInfo(id: Int) {
        api.getPublisherInfo(id).enqueue(object : Callback<Publisher?> {
            override fun onResponse(call: Call<Publisher?>, response: Response<Publisher?>) {
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
                        publisher = response.body()!!
                        Log.e("TAG", "onResponse: " + publisher.toString())
                        updateUi(publisher)
                    }
                }
            }

            override fun onFailure(call: Call<Publisher?>, t: Throwable) {
                Toast.makeText(applicationContext, t.message, Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun updateUi(info: Publisher) {
        info.name?.let { binding.tvDisplayName.text = it }
        info.email?.let { binding.tvEmail.text = it }
        info.address?.let { binding.tvAddress.text = it }
        info.website?.let { binding.tvWebsite.text = it }
        info.telephone?.let { binding.tvTelephone.text = it.toString() }
        bookLists = if (publisher.books != null) {
            publisher.books!!
        } else {
            arrayListOf()
        }
        setUpAdapter()
    }

    private fun setUpAdapter() {
        val recyclerView = binding.rcvPublisherBook
        val glm = GridLayoutManager(this, 2)
        recyclerView.layoutManager = glm
        bookAdapter = BookPreviewAdapter(bookLists, this, this)
        recyclerView.adapter = bookAdapter
        glm.spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return 1
            }
        }
        binding.shimmerViewContainer.visibility = View.GONE
    }

    override fun onItemViewClick(pos: Int) {
        val intent = Intent(this, BookDetailActivity::class.java).apply {
            putExtra(Constants.EXTRA_MESSAGE, bookLists[pos].id)
            addFlags(Intent.FLAG_ACTIVITY_REORDER_TO_FRONT)
        }
        startActivity(intent)
    }

    override fun onBookStateClick(pos: Int, view: View) {
        showOverflowMenu(pos, view)
    }

    private fun showOverflowMenu(pos: Int, anchor: View) {
        val menu = PopupMenu(this, anchor)
        menu.menu.apply {
            add("None").setOnMenuItemClickListener {
                //bookLists[pos].state = "None"
                bookAdapter.notifyItemChanged(pos)
                true
            }
            add("Read").setOnMenuItemClickListener {
                //bookLists[pos].state = "Read"
                bookAdapter.notifyItemChanged(pos)
                true
            }
            add("Currently Reading").setOnMenuItemClickListener {
                //bookLists[pos].state = "Currently Reading"
                bookAdapter.notifyItemChanged(pos)
                true
            }

            add("Want To Read").setOnMenuItemClickListener {
                //bookLists[pos].state = "Want To Read"
                bookAdapter.notifyItemChanged(pos)
                true
            }
        }
        menu.show()
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

