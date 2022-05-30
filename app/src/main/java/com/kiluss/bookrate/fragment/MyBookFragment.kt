package com.kiluss.bookrate.fragment

import android.content.Intent
import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.appcompat.widget.PopupMenu
import androidx.recyclerview.widget.GridLayoutManager
import com.kiluss.bookrate.utils.Const.Companion.EXTRA_MESSAGE
import com.kiluss.bookrate.activity.BookDetailActivity
import com.kiluss.bookrate.activity.MyBookSearchActivity
import com.kiluss.bookrate.adapter.BookPreviewAdapter
import com.kiluss.bookrate.adapter.BookPreviewAdapterInterface
import com.kiluss.bookrate.databinding.FragmentMyBookBinding
import com.kiluss.bookrate.data.model.BookModel

class MyBookFragment : Fragment(), BookPreviewAdapterInterface {
    private lateinit var bookLists: List<BookModel>
    private var _binding: FragmentMyBookBinding? = null
    private val binding get() = _binding!!
    private lateinit var bookAdapter: BookPreviewAdapter

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentMyBookBinding.inflate(inflater, container, false)
        val recyclerView = binding.rcvMyBook
        recyclerView.layoutManager = GridLayoutManager(context, 2)
        bookLists = listOf(

        )
        bookAdapter = BookPreviewAdapter(bookLists, this.requireContext(), this)
        recyclerView.adapter = bookAdapter
        binding.btnSearch.setOnClickListener {
            val intent = Intent(this.requireContext(), MyBookSearchActivity::class.java)
            startActivity(intent)
        }
        binding.llBookState.setOnClickListener {
            showOverflowMenuSearchBookState()
        }
        shouldShowEmptyBookString()
        return binding.root
    }

    private fun shouldShowEmptyBookString() {
        if (bookLists.isEmpty()) {
            binding.tvEmptyBook.visibility = View.VISIBLE
        } else {
            binding.tvEmptyBook.visibility = View.GONE
        }
    }

    override fun onItemViewClick(pos: Int) {
        val message = bookLists[pos].name
        val intent = Intent(this.requireContext(), BookDetailActivity::class.java).apply {
            putExtra(EXTRA_MESSAGE, message)
        }
        startActivity(intent)
    }

    override fun onBookStateClick(pos: Int, view: View) {
        showOverflowMenuItemBook(pos, view)
    }

    private fun showOverflowMenuSearchBookState() {
        val menu = PopupMenu(requireContext(), binding.llBookState)
        menu.menu.apply {
            add("All State").setOnMenuItemClickListener {
                binding.tvBookState.text = "All State"
                true
            }
            add("Read").setOnMenuItemClickListener {
                binding.tvBookState.text = "Read"
                    true
            }
            add("Currently Reading").setOnMenuItemClickListener {
                binding.tvBookState.text ="Currently Reading"
                    true
            }

            add("Want To Read").setOnMenuItemClickListener {
                binding.tvBookState.text ="Want To Read"
                    true
            }
        }
        menu.show()
    }

    private fun showOverflowMenuItemBook(pos: Int, anchor: View) {
        val menu = PopupMenu(requireContext(), anchor)
//        menu.menu.apply {
//            add("None").setOnMenuItemClickListener {
//                bookLists[pos].state = "None"
//                bookAdapter.notifyItemChanged(pos)
//                true
//            }
//            add("Read").setOnMenuItemClickListener {
//                bookLists[pos].state = "Read"
//                bookAdapter.notifyItemChanged(pos)
//                true
//            }
//            add("Currently Reading").setOnMenuItemClickListener {
//                bookLists[pos].state = "Currently Reading"
//                bookAdapter.notifyItemChanged(pos)
//                true
//            }
//
//            add("Want To Read").setOnMenuItemClickListener {
//                bookLists[pos].state = "Want To Read"
//                bookAdapter.notifyItemChanged(pos)
//                true
//            }
//        }
        menu.show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}