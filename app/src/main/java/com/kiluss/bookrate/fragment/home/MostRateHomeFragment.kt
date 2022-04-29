package com.kiluss.bookrate.fragment.home

import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kiluss.bookrate.R
import com.kiluss.bookrate.adapter.BookPreviewAdapter
import com.kiluss.bookrate.adapter.BookPreviewAdapterInterface
import com.kiluss.model.BookModel

class MostRateHomeFragment : Fragment(), BookPreviewAdapterInterface {
    private lateinit var mBookLists: List<BookModel>

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_most_rate_home, container, false)

        val recyclerView = view.findViewById<RecyclerView>(R.id.rcvMostRateHome)
        recyclerView.layoutManager = GridLayoutManager(context, 3)
        mBookLists = listOf(
            BookModel("Fake data"), BookModel("Fake title"),
            BookModel("Fake data"), BookModel("Fake title"),
            BookModel("Fake data"), BookModel("Fake title"),
            BookModel("Fake data"), BookModel("Fake title"),
            BookModel("Fake data"), BookModel("Fake title"),
            BookModel("Fake data"), BookModel("Fake title"),
        )
        val mBookPreviewAdapter = BookPreviewAdapter(mBookLists, this.requireContext(), this)
        recyclerView.adapter = mBookPreviewAdapter
        return view
    }

    override fun onItemClick(pos: Int) {
        Toast.makeText(this.requireContext(), "Go to detail page " + pos, Toast.LENGTH_SHORT).show()
    }
}