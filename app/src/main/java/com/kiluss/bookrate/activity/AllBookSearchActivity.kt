package com.kiluss.bookrate.activity

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.util.JsonToken
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.recyclerview.widget.GridLayoutManager
import com.google.gson.Gson
import com.google.gson.JsonObject
import com.kiluss.bookrate.R
import com.kiluss.bookrate.adapter.BookPreviewAdapter
import com.kiluss.bookrate.adapter.BookPreviewAdapterInterface
import com.kiluss.bookrate.data.model.BookModel
import com.kiluss.bookrate.data.model.LoginResponse
import com.kiluss.bookrate.databinding.ActivityAllBookSearchBinding
import com.kiluss.bookrate.network.api.BookService
import com.kiluss.bookrate.network.api.RetrofitClient
import com.kiluss.bookrate.utils.Constants.Companion.EXTRA_MESSAGE
import okhttp3.RequestBody
import org.json.JSONException
import org.json.JSONObject
import org.json.JSONStringer
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class AllBookSearchActivity : AppCompatActivity(), BookPreviewAdapterInterface {

    private lateinit var bookAdapter: BookPreviewAdapter
    private lateinit var binding: ActivityAllBookSearchBinding
    private var bookLists = arrayListOf<BookModel>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityAllBookSearchBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.tvEmptyResult.text = "This will be fast.."
        intent.getStringExtra(EXTRA_MESSAGE)?.let { search(it) }
    }

    private fun search(query: String) {
        getLoginResponse(this).token?.let {
            RetrofitClient.getInstance(this).getClientAuthorized(it)
                .create(BookService::class.java).searchBook(createRequestBodyForBookRequest(query))
                .enqueue(object : Callback<ArrayList<BookModel>?> {
                    override fun onResponse(
                        call: Call<ArrayList<BookModel>?>,
                        response: Response<ArrayList<BookModel>?>
                    ) {
                        when {
                            response.code() == 404 -> {
                                Toast.makeText(
                                    this@AllBookSearchActivity,
                                    "Url is not exist",
                                    Toast.LENGTH_SHORT
                                ).show()
                                binding.tvEmptyResult.text = getString(R.string.hmm_seem_to_be_nothing_like_this)
                            }
                            response.code() == 400 -> {
                                Toast.makeText(
                                    this@AllBookSearchActivity,
                                    "Bad request",
                                    Toast.LENGTH_SHORT
                                ).show()
                                binding.tvEmptyResult.text = getString(R.string.hmm_seem_to_be_nothing_like_this)
                            }
                            response.isSuccessful -> {
                                response.body()?.let {
                                    bookLists.clear()
                                    bookLists.addAll(it)
                                    initView()
                                    if (it.isEmpty()) {
                                        binding.tvEmptyResult.text = getString(R.string.hmm_seem_to_be_nothing_like_this)
                                    } else
                                        binding.tvEmptyResult.visibility = View.GONE
                                }
                            }
                        }
                    }

                    override fun onFailure(call: Call<ArrayList<BookModel>?>, t: Throwable) {
                        Toast.makeText(this@AllBookSearchActivity, t.message, Toast.LENGTH_LONG).show()
                        binding.tvEmptyResult.text = getString(R.string.hmm_seem_to_be_nothing_like_this)
                    }
                })
        }
    }

    private fun createRequestBodyForBookRequest(query: String) = run {

        val json = Gson()
        json.toJson(query)
        Log.e("search json", json.toJson(query).toString())
        RequestBody.create(
            okhttp3.MediaType.parse("application/json; charset=utf-8"),
            json.toJson(query).toString()
        )
    }

    private fun initView() {
        bookAdapter = BookPreviewAdapter(bookLists, this, this)
        setRVLayoutManager()
    }

    private fun setRVLayoutManager() {
        binding.rcvSearchResult.apply {
            layoutManager = GridLayoutManager(this@AllBookSearchActivity, 2)
            setHasFixedSize(true)
            adapter = bookAdapter
        }
    }

    private fun getLoginResponse(context: Context) : LoginResponse {
        val sharedPref = context.getSharedPreferences(
            context.getString(R.string.saved_login_account_key),
            Context.MODE_PRIVATE
        )
        val gson = Gson()
        val json: String? = sharedPref.getString(context.getString(R.string.saved_login_account_key), "")
        return gson.fromJson(json, LoginResponse::class.java)
    }

    override fun onItemViewClick(pos: Int) {
        val intent = Intent(this, BookDetailActivity::class.java).apply {
            putExtra(EXTRA_MESSAGE, bookLists[pos].id)
        }
        startActivity(intent)
    }

    override fun onBookStateClick(pos: Int, view: View, bookState: Int) {

    }
}