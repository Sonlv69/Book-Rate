package com.kiluss.bookrate.fragment.home

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.recyclerview.widget.GridLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kiluss.bookrate.R
import com.kiluss.bookrate.adapter.BookPreviewAdapter
import com.kiluss.bookrate.adapter.BookPreviewAdapterInterface
import com.kiluss.model.BookModel

class TrendingHomeFragment : Fragment(), BookPreviewAdapterInterface {
    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        // Inflate the layout for this fragment
        val view = inflater.inflate(R.layout.fragment_trending_home, container, false)

        val recyclerView = view.findViewById<RecyclerView>(R.id.rcvTrendingHome)
        recyclerView.layoutManager = GridLayoutManager(context, 3)
        val mBookLists: List<BookModel> = listOf(
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
        Toast.makeText(this.requireContext(), "Go to detail page " + (pos + 1), Toast.LENGTH_SHORT).show()
    }
}