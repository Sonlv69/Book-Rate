package com.kiluss.bookrate.fragment.home

import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.appcompat.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.kiluss.bookrate.utils.Constants.Companion.EXTRA_MESSAGE
import com.kiluss.bookrate.activity.BookDetailActivity
import com.kiluss.bookrate.adapter.BookPreviewAdapterInterface
import com.kiluss.bookrate.adapter.BookPreviewLoadMoreAdapter
import com.kiluss.bookrate.data.model.BookModel
import com.kiluss.bookrate.databinding.FragmentMostRecentHomeBinding
import com.kiluss.bookrate.network.api.BookService
import com.kiluss.bookrate.network.api.RetrofitClient
import com.kiluss.bookrate.utils.Constants.Companion.VIEW_TYPE_ITEM
import com.kiluss.bookrate.utils.Constants.Companion.VIEW_TYPE_LOADING
import com.kiluss.bookrate.utils.OnLoadMoreListener
import com.kiluss.bookrate.utils.RecyclerViewLoadMoreScroll
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class AllBookHomeFragment : Fragment(), BookPreviewAdapterInterface {
    private lateinit var layoutManager: GridLayoutManager
    private var bookLists: ArrayList<BookModel?> = arrayListOf()
    private var _binding: FragmentMostRecentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var bookAdapter: BookPreviewLoadMoreAdapter
    lateinit var scrollListener: RecyclerViewLoadMoreScroll
    private var page: Int = 0

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMostRecentHomeBinding.inflate(inflater, container, false)
        init()
        return binding.root
    }

    private fun init() {
        bookAdapter = BookPreviewLoadMoreAdapter(requireContext(), this)
        setRVLayoutManager()
        setRVScrollListener()
        loadMoreData()
    }

    private fun setRVLayoutManager() {
        layoutManager = GridLayoutManager(requireContext(), 2)
        binding.rcvMostRateHome.layoutManager = layoutManager
        binding.rcvMostRateHome.setHasFixedSize(true)
        binding.rcvMostRateHome.adapter = bookAdapter
        (layoutManager).spanSizeLookup = object : GridLayoutManager.SpanSizeLookup() {
            override fun getSpanSize(position: Int): Int {
                return when (bookAdapter.getItemViewType(position)) {
                    VIEW_TYPE_ITEM -> 1
                    VIEW_TYPE_LOADING -> 2 //number of columns of the grid
                    else -> -1
                }
            }
        }
    }

    private fun setRVScrollListener() {
        scrollListener = RecyclerViewLoadMoreScroll(layoutManager)
        scrollListener.setOnLoadMoreListener(object :
            OnLoadMoreListener {
            override fun onLoadMore() {
                loadMoreData()
            }
        })

        binding.rcvMostRateHome.addOnScrollListener(scrollListener)
    }

    private fun loadMoreData() {
        //Add the Loading View
        bookAdapter.addLoadingView()
        page++
        RetrofitClient.getInstance(requireContext()).getClientUnAuthorize()
            .create(BookService::class.java).getAllBooks(page)
            .enqueue(object : Callback<ArrayList<BookModel?>?> {
                override fun onResponse(
                    call: Call<ArrayList<BookModel?>?>,
                    response: Response<ArrayList<BookModel?>?>
                ) {
                    _binding?.let {
                        //Remove the Loading View
                        bookAdapter.removeLoadingView()
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
                                        bookLists.addAll(it)
                                        bookAdapter.addData(it)
                                    }
                                }
                            }
                        }
                        //Change the boolean isLoading to false
                        scrollListener.setLoaded()
                        binding.shimmerViewContainer.visibility = View.GONE
                    }
                }

                override fun onFailure(call: Call<ArrayList<BookModel?>?>, t: Throwable) {
                    _binding?.let {
                        Toast.makeText(context, t.message, Toast.LENGTH_LONG).show()
                        binding.shimmerViewContainer.visibility = View.GONE
                    }
                }
            })
    }

    override fun onItemViewClick(pos: Int) {
        val intent = Intent(this.requireContext(), BookDetailActivity::class.java).apply {
            putExtra(EXTRA_MESSAGE, bookLists[pos]?.id)
        }
        startActivity(intent)
    }

    override fun onBookStateClick(pos: Int, view: View, bookState: Int) {
//        showOverflowMenu(pos, view)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        page = 0
        _binding = null
    }
}