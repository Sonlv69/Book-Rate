package com.kiluss.bookrate.fragment.home

import android.content.Intent
import android.os.Bundle
import android.view.*
import android.widget.Toast
import androidx.appcompat.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.kiluss.bookrate.utils.Const.Companion.EXTRA_MESSAGE
import com.kiluss.bookrate.activity.BookDetailActivity
import com.kiluss.bookrate.adapter.BookPreviewAdapter
import com.kiluss.bookrate.adapter.BookPreviewAdapterInterface
import com.kiluss.bookrate.data.model.BookModel
import com.kiluss.bookrate.databinding.FragmentMostRecentHomeBinding
import com.kiluss.bookrate.network.api.BookService
import com.kiluss.bookrate.network.api.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class MostRecentHomeFragment : Fragment(), BookPreviewAdapterInterface {
    private lateinit var bookLists: ArrayList<BookModel>
    private var _binding: FragmentMostRecentHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var bookAdapter: BookPreviewAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMostRecentHomeBinding.inflate(inflater, container, false)
        RetrofitClient.getInstance(requireContext()).getClientUnAuthorize()
            .create(BookService::class.java).getAllBooks()
            .enqueue(object : Callback<ArrayList<BookModel>?> {
                override fun onResponse(
                    call: Call<ArrayList<BookModel>?>,
                    response: Response<ArrayList<BookModel>?>
                ) {
                    when {
                        response.code() == 404 -> {
                            Toast.makeText(
                                context,
                                "Url is not exist",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        response.isSuccessful && _binding != null -> {
                            bookLists = response.body()!!
                            setUpAdapter()
                            binding.shimmerViewContainer.visibility = View.GONE
                        }
                    }
                }

                override fun onFailure(call: Call<ArrayList<BookModel>?>, t: Throwable) {
                    Toast.makeText(context, t.message, Toast.LENGTH_LONG).show()
                    binding.shimmerViewContainer.visibility = View.GONE
                }
            })
        return binding.root
    }

    private fun setUpAdapter() {
        val recyclerView = binding.rcvMostRateHome
        recyclerView.layoutManager = GridLayoutManager(context, 2)
        bookAdapter = BookPreviewAdapter(bookLists, this.requireContext(), this)
        recyclerView.adapter = bookAdapter
    }

    override fun onItemViewClick(pos: Int) {
        val intent = Intent(this.requireContext(), BookDetailActivity::class.java).apply {
            putExtra(EXTRA_MESSAGE, bookLists[pos].id)
        }
        startActivity(intent)
    }

    override fun onBookStateClick(pos: Int, view: View) {
        showOverflowMenu(pos, view)
    }

    private fun showOverflowMenu(pos: Int, anchor: View) {
        val menu = PopupMenu(requireContext(), anchor)
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

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}