package com.kiluss.bookrate.fragment.home

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.google.gson.Gson
import com.kiluss.bookrate.R
import com.kiluss.bookrate.utils.Constants.Companion.EXTRA_MESSAGE
import com.kiluss.bookrate.activity.BookDetailActivity
import com.kiluss.bookrate.adapter.BookPreviewAdapterInterface
import com.kiluss.bookrate.adapter.BookPreviewTopReviewAdapter
import com.kiluss.bookrate.data.model.LoginResponse
import com.kiluss.bookrate.data.model.TopReview
import com.kiluss.bookrate.databinding.FragmentMostRecentHomeBinding
import com.kiluss.bookrate.network.api.BookService
import com.kiluss.bookrate.network.api.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class TopReviewHomeFragment : Fragment(), BookPreviewAdapterInterface {
    private lateinit var layoutManager: GridLayoutManager
    private var bookLists: ArrayList<TopReview> = arrayListOf()
    private var _binding: FragmentMostRecentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var bookAdapter: BookPreviewTopReviewAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMostRecentHomeBinding.inflate(inflater, container, false)
        loadData()
        return binding.root
    }

    private fun initView() {
        bookAdapter = BookPreviewTopReviewAdapter(bookLists, requireContext(), this)
        setRVLayoutManager()
    }

    private fun setRVLayoutManager() {
        layoutManager = GridLayoutManager(requireContext(), 2)
        binding.rcvMostRateHome.layoutManager = layoutManager
        binding.rcvMostRateHome.setHasFixedSize(true)
        binding.rcvMostRateHome.adapter = bookAdapter
    }

    private fun loadData() {
        getLoginResponse(requireContext()).token?.let {
            RetrofitClient.getInstance(requireContext()).getClientAuthorized(it)
                .create(BookService::class.java).getStatisticalReview()
                .enqueue(object : Callback<ArrayList<TopReview>?> {
                    override fun onResponse(
                        call: Call<ArrayList<TopReview>?>,
                        response: Response<ArrayList<TopReview>?>
                    ) {
                        _binding?.let {
                            when {
                                response.code() == 404 -> {
                                    Toast.makeText(
                                        context,
                                        "Url is not exist",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                                response.code() == 400 -> {
                                    Toast.makeText(
                                        context,
                                        "Bad request",
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                                response.isSuccessful && _binding != null -> {
                                    if (response.body()?.size == 0) {
                                        Toast.makeText(
                                            context,
                                            "Last of result",
                                            Toast.LENGTH_SHORT
                                        ).show()
                                    } else {
                                        response.body()?.let {
                                            bookLists.clear()
                                            bookLists.addAll(it)
                                            initView()
                                        }
                                    }
                                }
                            }
                            binding.shimmerViewContainer.visibility = View.GONE
                        }
                    }

                    override fun onFailure(call: Call<ArrayList<TopReview>?>, t: Throwable) {
                        _binding?.let {
                            Toast.makeText(context, t.message, Toast.LENGTH_LONG).show()
                            binding.shimmerViewContainer.visibility = View.GONE
                        }
                    }
                })
        }
    }

    override fun onItemViewClick(pos: Int) {
        val intent = Intent(this.requireContext(), BookDetailActivity::class.java).apply {
            putExtra(EXTRA_MESSAGE, bookLists[pos].iDBook)
        }
        startActivity(intent)
    }

    override fun onBookStateClick(pos: Int, view: View, bookState: Int) {

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

    override fun onResume() {
        super.onResume()
        loadData()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}