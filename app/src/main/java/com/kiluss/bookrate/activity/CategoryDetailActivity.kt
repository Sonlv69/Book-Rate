package com.kiluss.bookrate.activity

import android.content.Context
import android.content.Intent
import android.os.Build
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.annotation.RequiresApi
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.GridLayoutManager
import com.google.gson.Gson
import com.kiluss.bookrate.R
import com.kiluss.bookrate.adapter.BookPreviewAdapter
import com.kiluss.bookrate.adapter.BookPreviewAdapterInterface
import com.kiluss.bookrate.data.model.BookModel
import com.kiluss.bookrate.data.model.LoginResponse
import com.kiluss.bookrate.data.model.Tag
import com.kiluss.bookrate.databinding.ActivityCategoryDetailBinding
import com.kiluss.bookrate.network.api.BookService
import com.kiluss.bookrate.network.api.RetrofitClient
import com.kiluss.bookrate.utils.Const
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.util.stream.Collectors

class CategoryDetailActivity : AppCompatActivity(), BookPreviewAdapterInterface {
    private lateinit var binding: ActivityCategoryDetailBinding
    private lateinit var api: BookService
    private lateinit var tag: Tag
    private lateinit var bookLists: ArrayList<BookModel>
    private lateinit var bookAdapter: BookPreviewAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityCategoryDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val idTag = intent.getIntExtra(Const.EXTRA_MESSAGE, 0)
        val loginResponse = getLoginResponse(this)
        api = RetrofitClient.getInstance(this).getClientAuthorized(loginResponse.token.toString())
            .create(BookService::class.java)
        getCategoryInfo(idTag)
    }

    private fun getCategoryInfo(id: Int) {
        api.getTagInfo(id).enqueue(object : Callback<Tag?> {
            @RequiresApi(Build.VERSION_CODES.N)
            override fun onResponse(call: Call<Tag?>, response: Response<Tag?>) {
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
                        tag = response.body()!!
                        Log.e("TAG", "onResponse: " + tag.toString())
                        updateUi(tag)
                    }
                }
            }

            override fun onFailure(call: Call<Tag?>, t: Throwable) {
                Toast.makeText(applicationContext, t.message, Toast.LENGTH_LONG).show()
            }
        })
    }

    @RequiresApi(Build.VERSION_CODES.N)
    private fun updateUi(info: Tag) {
        info.name?.let { binding.tvDisplayName.text = it }
        info.description?.let { binding.tvDescription.text = it }
        bookLists = if (tag.books != null) {
            tag.books!!.stream().map {tags -> tags.book}.collect(Collectors.toList()) as ArrayList<BookModel>
        } else {
            arrayListOf()
        }
        info.description?.let { binding.tvDescription.text = it }
        setUpAdapter()
    }

    private fun setUpAdapter() {
        val recyclerView = binding.rcvCategoryBook
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
            putExtra(Const.EXTRA_MESSAGE, bookLists[pos].id)
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

    private fun convertDateTime(jsonDate: String): String {
        return jsonDate.split("T")[0]
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