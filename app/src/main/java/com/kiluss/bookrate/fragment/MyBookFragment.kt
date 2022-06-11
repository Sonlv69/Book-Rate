package com.kiluss.bookrate.fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.appcompat.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.google.gson.Gson
import com.kiluss.bookrate.R
import com.kiluss.bookrate.activity.BookDetailActivity
import com.kiluss.bookrate.activity.MyBookSearchActivity
import com.kiluss.bookrate.adapter.BookPreviewAdapterInterface
import com.kiluss.bookrate.adapter.MyBookPreviewAdapter
import com.kiluss.bookrate.data.model.LoginResponse
import com.kiluss.bookrate.data.model.MyBookState
import com.kiluss.bookrate.databinding.FragmentMyBookBinding
import com.kiluss.bookrate.network.api.BookService
import com.kiluss.bookrate.network.api.RetrofitClient
import com.kiluss.bookrate.utils.Constants
import com.kiluss.bookrate.utils.Constants.Companion.CURRENTLY_READING
import com.kiluss.bookrate.utils.Constants.Companion.EXTRA_MESSAGE
import com.kiluss.bookrate.utils.Constants.Companion.READ
import com.kiluss.bookrate.utils.Constants.Companion.WANT_TO_READ
import com.kiluss.bookrate.viewmodel.MainActivityViewModel
import okhttp3.RequestBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MyBookFragment : Fragment(), BookPreviewAdapterInterface {
    private lateinit var bookLists: MutableList<MyBookState>
    private var _binding: FragmentMyBookBinding? = null
    private val binding get() = _binding!!
    private lateinit var bookAdapter: MyBookPreviewAdapter
    private lateinit var apiAuthorized: BookService
    private var bookState = 0
    private var myQuery = 0
    private val viewModel: MainActivityViewModel by activityViewModels()

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMyBookBinding.inflate(inflater, container, false)
        apiAuthorized = RetrofitClient.getInstance(requireContext())
            .getClientAuthorized(getLoginResponse().token!!)
            .create(BookService::class.java)
        binding.btnSearch.setOnClickListener {
            val intent = Intent(this.requireContext(), MyBookSearchActivity::class.java)
            startActivity(intent)
        }
        bookLists = mutableListOf()
        getMyBook()
        return binding.root
    }

    private fun getLoginResponse(): LoginResponse {
        val sharedPref = requireContext().getSharedPreferences(
            requireContext().getString(R.string.saved_login_account_key),
            Context.MODE_PRIVATE
        )
        val gson = Gson()
        val json: String? =
            sharedPref.getString(requireContext().getString(R.string.saved_login_account_key), "")
        return gson.fromJson(json, LoginResponse::class.java)
    }

    private fun getMyBook() {
        apiAuthorized.getMyBook().enqueue(object : Callback<ArrayList<MyBookState>> {
            override fun onResponse(
                call: Call<ArrayList<MyBookState>>,
                response: Response<ArrayList<MyBookState>>
            ) {
                when {
                    response.code() == 400 -> {
                        Toast.makeText(
                            requireContext(),
                            "Bad request",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    response.code() == 404 -> {
                        Toast.makeText(
                            requireContext(),
                            "Url is not exist",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    response.code() == 500 -> {
                        Toast.makeText(
                            requireContext(),
                            "Internal error",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    response.isSuccessful -> {
                        response.body()?.let {
                            bookLists = it
                            setUpRecyclerView()
                        }
                        shouldShowEmptyBookString()
                        binding.llBookState.setOnClickListener {
                            showOverflowMenuSearchBookState()
                        }
                    }
                }
            }

            override fun onFailure(call: Call<ArrayList<MyBookState>>, t: Throwable) {
                Toast.makeText(
                    requireContext(),
                    t.message,
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

    private fun postMyBook(pos: Int) {
        bookLists[pos].iDBook?.let { createRequestBodyForPostMyBook(it) }?.let {
            apiAuthorized.postMyBook(it).enqueue(object : Callback<MyBookState> {
                override fun onResponse(call: Call<MyBookState>, response: Response<MyBookState>) {
                    when {
                        response.code() == 400 -> {
                            Toast.makeText(
                                requireContext(),
                                "Bad request",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        response.code() == 404 -> {
                            Toast.makeText(
                                requireContext(),
                                "Url is not exist",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        response.code() == 500 -> {
                            Toast.makeText(
                                requireContext(),
                                "Internal error",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        response.isSuccessful -> {
                            if (bookState == 0) {
                                Toast.makeText(
                                    requireContext(),
                                    "Added to my book",
                                    Toast.LENGTH_SHORT
                                ).show()
                            } else {
                                Toast.makeText(
                                    requireContext(),
                                    "Change book state",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                            bookLists[pos].statusBook = 1
                            bookAdapter.notifyItemChanged(pos)
                        }
                    }
                }

                override fun onFailure(call: Call<MyBookState>, t: Throwable) {
                    Toast.makeText(
                        requireContext(),
                        t.message,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
        }
    }

    private fun putBookState(state: Int, pos: Int) {
        bookLists[pos].iDBook?.let { createRequestBodyForPutMyBook(it, state) }?.let {
            apiAuthorized.putMyBook(it).enqueue(object : Callback<MyBookState> {
                override fun onResponse(call: Call<MyBookState>, response: Response<MyBookState>) {
                    when {
                        response.code() == 400 -> {
                            Toast.makeText(
                                requireContext(),
                                "Bad request",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        response.code() == 404 -> {
                            Toast.makeText(
                                requireContext(),
                                "Url is not exist",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        response.code() == 500 -> {
                            Toast.makeText(
                                requireContext(),
                                "Internal error",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        response.isSuccessful -> {
                            if (bookState != 0) {
                                Toast.makeText(
                                    requireContext(),
                                    "Change book state",
                                    Toast.LENGTH_SHORT
                                ).show()
                            }
                            bookState = state
                            bookLists[pos].statusBook = state
                            if (bookLists[pos].statusBook != 0 && bookLists[pos].statusBook != myQuery) {
                                bookLists.removeAt(pos)
                            }
                            bookAdapter.notifyItemChanged(pos)
                        }
                    }
                }

                override fun onFailure(call: Call<MyBookState>, t: Throwable) {
                    Toast.makeText(
                        requireContext(),
                        t.message,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
        }
    }

    private fun deleteFromMyBook(pos: Int) {
        bookLists[pos].iDBook?.let {
            apiAuthorized.deleteMyBookById(it).enqueue(object : Callback<Unit> {
                override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
                    when {
                        response.code() == 400 -> {
                            Toast.makeText(
                                requireContext(),
                                "Bad request",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        response.code() == 404 -> {
                            Toast.makeText(
                                requireContext(),
                                "Url is not exist",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        response.code() == 500 -> {
                            Toast.makeText(
                                requireContext(),
                                "Internal error",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        response.isSuccessful -> {
                            Toast.makeText(
                                requireContext(),
                                "Remove from my book",
                                Toast.LENGTH_SHORT
                            ).show()
                            bookState = 0
                            bookLists.removeAt(pos)
                            bookAdapter.notifyItemRemoved(pos)
                            viewModel.getMyBookSize(requireContext())
                        }
                    }
                }

                override fun onFailure(call: Call<Unit>, t: Throwable) {
                    Toast.makeText(
                        requireContext(),
                        t.message,
                        Toast.LENGTH_SHORT
                    ).show()
                }
            })
        }
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

    private fun setUpRecyclerView() {
        binding.rcvMyBook.layoutManager = GridLayoutManager(context, 2)
        bookAdapter = MyBookPreviewAdapter(bookLists, this.requireContext(), this)
        binding.rcvMyBook.adapter = bookAdapter
    }

    private fun shouldShowEmptyBookString() {
        if (bookLists.isEmpty()) {
            binding.tvEmptyBook.visibility = View.VISIBLE
            binding.tvEmptyBook.text = getString(R.string.your_bookshelves_is_empty)
        } else {
            binding.tvEmptyBook.visibility = View.GONE
        }
    }

    override fun onItemViewClick(pos: Int) {
        val intent = Intent(this.requireContext(), BookDetailActivity::class.java).apply {
            putExtra(EXTRA_MESSAGE, bookLists[pos].book?.id)
        }
        startActivity(intent)
    }

    override fun onBookStateClick(pos: Int, view: View, bookState: Int) {
        this@MyBookFragment.bookState = bookState
        showOverflowMenu(pos, view)
    }

    private fun showOverflowMenuSearchBookState() {
        val menu = PopupMenu(requireContext(), binding.llBookState)
        menu.menu.apply {
            add("All State").setOnMenuItemClickListener {
                myQuery = 0
                queryBookList(myQuery)
                true
            }
            add(WANT_TO_READ).setOnMenuItemClickListener {
                myQuery = 1
                queryBookList(myQuery)
                true
            }
            add(CURRENTLY_READING).setOnMenuItemClickListener {
                myQuery = 2
                queryBookList(myQuery)
                true
            }

            add(READ).setOnMenuItemClickListener {
                myQuery = 3
                queryBookList(myQuery)
                true
            }
        }
        menu.show()
    }

    private fun queryBookList(state: Int) {
        apiAuthorized.getMyBook().enqueue(object : Callback<ArrayList<MyBookState>> {
            override fun onResponse(
                call: Call<ArrayList<MyBookState>>,
                response: Response<ArrayList<MyBookState>>
            ) {
                when {
                    response.code() == 400 -> {
                        Toast.makeText(
                            requireContext(),
                            "Bad request",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    response.code() == 404 -> {
                        Toast.makeText(
                            requireContext(),
                            "Url is not exist",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    response.code() == 500 -> {
                        Toast.makeText(
                            requireContext(),
                            "Internal error",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    response.isSuccessful -> {
                        val newList = mutableListOf<MyBookState>()
                        val queryList = mutableListOf<MyBookState>()
                        response.body()?.let {
                            newList.addAll(it)
                        }
                        when (state) {
                            0 -> {
                                binding.tvBookState.text = "All State"
                                queryList.addAll(newList)
                            }
                            1 -> {
                                binding.tvBookState.text = WANT_TO_READ
                                newList.forEach {
                                    if (it.statusBook == 1) {
                                        queryList.add(it)
                                    }
                                }
                            }
                            2 -> {
                                binding.tvBookState.text = CURRENTLY_READING
                                newList.forEach {
                                    if (it.statusBook == 2) {
                                        queryList.add(it)
                                    }
                                }
                            }
                            3 -> {
                                binding.tvBookState.text = READ
                                newList.forEach {
                                    if (it.statusBook == 3) {
                                        queryList.add(it)
                                    }
                                }
                            }
                        }
                        bookLists.clear()
                        bookLists.addAll(queryList)
                        bookAdapter.changeData(queryList)
                        shouldShowEmptyBookString()
                    }
                }
            }

            override fun onFailure(call: Call<ArrayList<MyBookState>>, t: Throwable) {
                Toast.makeText(
                    requireContext(),
                    t.message,
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

    private fun showOverflowMenu(pos: Int, anchor: View) {
        val menu = PopupMenu(requireContext(), anchor)
        menu.menu.apply {
            add(Constants.UN_READ).setOnMenuItemClickListener {
                changeBookState(0, pos)
                true
            }
            add(WANT_TO_READ).setOnMenuItemClickListener {
                changeBookState(1, pos)
                true
            }
            add(CURRENTLY_READING).setOnMenuItemClickListener {
                changeBookState(2, pos)
                true
            }
            add(READ).setOnMenuItemClickListener {
                changeBookState(3, pos)
                true
            }
        }
        menu.show()
    }

    private fun changeBookState(newState: Int, pos: Int) {
        if (bookState == newState) {
            return
        }
        if (bookState == 0) {
            postMyBook(pos)
            if (newState == 1) {
                return
            }
        }
        when (newState) {
            0 -> {
                deleteFromMyBook(pos)
            }
            1 -> {
                putBookState(1, pos)
            }
            2 -> {
                putBookState(2, pos)
            }
            else -> {
                putBookState(3, pos)
            }
        }
    }

    override fun onResume() {
        super.onResume()
        if (::bookAdapter.isInitialized) {
            queryBookList(myQuery)
        } else {
            getMyBook()
        }
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}