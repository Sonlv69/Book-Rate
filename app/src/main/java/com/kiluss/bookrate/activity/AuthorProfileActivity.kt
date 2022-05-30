package com.kiluss.bookrate.activity

import android.content.Context
import android.content.Intent
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.widget.PopupMenu
import androidx.core.content.ContextCompat
import androidx.recyclerview.widget.GridLayoutManager
import com.google.gson.Gson
import com.kiluss.bookrate.R
import com.kiluss.bookrate.adapter.BookPreviewAdapter
import com.kiluss.bookrate.adapter.BookPreviewAdapterInterface
import com.kiluss.bookrate.data.model.Account
import com.kiluss.bookrate.data.model.Author
import com.kiluss.bookrate.data.model.BookModel
import com.kiluss.bookrate.data.model.LoginResponse
import com.kiluss.bookrate.databinding.ActivityAuthorProfileBinding
import com.kiluss.bookrate.databinding.ActivityBookDetailBinding
import com.kiluss.bookrate.network.api.BookService
import com.kiluss.bookrate.network.api.RetrofitClient
import com.kiluss.bookrate.utils.Const.Companion.EXTRA_MESSAGE
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.MessageFormat

class AuthorProfileActivity : AppCompatActivity(), BookPreviewAdapterInterface {
    private lateinit var binding: ActivityAuthorProfileBinding
    private lateinit var api: BookService
    private lateinit var author: Author
    private lateinit var bookLists: ArrayList<BookModel>
    private lateinit var bookAdapter: BookPreviewAdapter
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAuthorProfileBinding.inflate(layoutInflater)
        setContentView(binding.root)
        val idAuthor = intent.getIntExtra(EXTRA_MESSAGE, 0)
        val loginResponse = getLoginResponse(this)
        api = RetrofitClient.getInstance(this).getClientAuthorized(loginResponse.token.toString())
            .create(BookService::class.java)
        getAuthorInfo(idAuthor)
    }

    private fun getAuthorInfo(id: Int) {
        api.getAuthorInfo(id).enqueue(object : Callback<Author?> {
            override fun onResponse(call: Call<Author?>, response: Response<Author?>) {
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
                        author = response.body()!!
                        Log.e("TAG", "onResponse: " + author.toString())
                        updateUi(author)
                    }
                }
            }

            override fun onFailure(call: Call<Author?>, t: Throwable) {
                Toast.makeText(applicationContext, t.message, Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun updateUi(info: Author) {
        binding.tvDisplayName.text = info.name
        info.stageName?.let { binding.tvStageName.text = info.stageName }
        info.birthday?.let { binding.tvBirthDay.text = convertDateTime(info.birthday.toString()) }
        bookLists = if (author.books != null) {
            author.books!!
        } else {
            arrayListOf()
        }
        if (info.description != null) {
            binding.tvDescription.text = info.description
        }
        setUpAdapter()
    }

    private fun setUpAdapter() {
        val recyclerView = binding.rcvAuthorBook
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
            putExtra(EXTRA_MESSAGE, bookLists[pos].id)
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

