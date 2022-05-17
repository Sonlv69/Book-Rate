package com.kiluss.bookrate.fragment.home

import android.content.Intent
import android.os.Bundle
import android.view.*
import androidx.appcompat.widget.PopupMenu
import androidx.fragment.app.Fragment
import androidx.recyclerview.widget.GridLayoutManager
import com.kiluss.bookrate.utils.Const.Companion.EXTRA_MESSAGE
import com.kiluss.bookrate.activity.BookDetailActivity
import com.kiluss.bookrate.adapter.BookPreviewAdapter
import com.kiluss.bookrate.adapter.BookPreviewAdapterInterface
import com.kiluss.bookrate.databinding.FragmentMostRateHomeBinding
import com.kiluss.bookrate.data.model.BookModel


class MostRateHomeFragment : Fragment(), BookPreviewAdapterInterface {
    private lateinit var bookLists: List<BookModel>
    private var _binding: FragmentMostRateHomeBinding? = null
    private val binding get() = _binding!!
    private lateinit var bookAdapter: BookPreviewAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMostRateHomeBinding.inflate(inflater, container, false)

        val recyclerView = binding.rcvMostRateHome
        recyclerView.layoutManager = GridLayoutManager(context, 2)
        bookLists = listOf(
            BookModel("The Adventures of Sherlock Holmes", "Want to read"), BookModel("Fake title", "None"),
            BookModel("The Adventures of Sherlock Holmes", "Want to read"), BookModel("Fake title", "None"),
            BookModel("The Adventures of Sherlock Holmes", "Want to read"), BookModel("Fake title", "None"),
            BookModel("The Adventures of Sherlock Holmes", "Want to read"), BookModel("Fake title", "None"),
            BookModel("The Adventures of Sherlock Holmes", "Want to read"), BookModel("Fake title", "None"),
            BookModel("The Adventures of Sherlock Holmes", "Want to read"), BookModel("Fake title", "None"),
            BookModel("The Adventures of Sherlock Holmes", "Want to read"), BookModel("Fake title", "None"),
        )
        bookAdapter = BookPreviewAdapter(bookLists, this.requireContext(), this)
        recyclerView.adapter = bookAdapter
        return binding.root
    }

    override fun onItemViewClick(pos: Int) {
        val message = bookLists[pos].bookTitle
        val intent = Intent(this.requireContext(), BookDetailActivity::class.java).apply {
            putExtra(EXTRA_MESSAGE, message)
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
                bookLists[pos].bookState = "None"
                bookAdapter.notifyItemChanged(pos)
                true
            }
            add("Read").setOnMenuItemClickListener {
                bookLists[pos].bookState = "Read"
                bookAdapter.notifyItemChanged(pos)
                true
            }
            add("Currently Reading").setOnMenuItemClickListener {
                bookLists[pos].bookState = "Currently Reading"
                bookAdapter.notifyItemChanged(pos)
                true
            }

            add("Want To Read").setOnMenuItemClickListener {
                bookLists[pos].bookState = "Want To Read"
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