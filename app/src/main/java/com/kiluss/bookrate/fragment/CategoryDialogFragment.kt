package com.kiluss.bookrate.fragment

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import androidx.fragment.app.DialogFragment
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.kiluss.bookrate.R
import com.kiluss.bookrate.activity.CategoryDetailActivity
import com.kiluss.bookrate.adapter.CategoryNameAdapter
import com.kiluss.bookrate.data.model.Tags
import com.kiluss.bookrate.utils.Const.Companion.EXTRA_MESSAGE

class CategoryDialogFragment : DialogFragment(), CategoryNameAdapter.CategoryNameAdapterInterface {
    private lateinit var categoryNameAdapter: CategoryNameAdapter
    private lateinit var recyclerView: RecyclerView
    private var param: ArrayList<Tags> = arrayListOf()

    companion object {
        @JvmStatic
        fun newInstance(param: ArrayList<Tags>) =
            CategoryDialogFragment().apply {
                arguments = Bundle().apply {
                    putParcelableArrayList(EXTRA_MESSAGE, param)
                }
            }
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        arguments?.getParcelableArrayList<Tags>(EXTRA_MESSAGE)?.let {
            param = it
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        return inflater.inflate(R.layout.fragment_category_dialog, container, false)
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        recyclerView = view.findViewById(R.id.rcvCategoryName)
        setupView(view)
        setFullScreen()
    }

    private fun setupView(view: View) {
        categoryNameAdapter = CategoryNameAdapter(param, this)
        recyclerView.layoutManager =
            LinearLayoutManager(requireContext(), LinearLayoutManager.VERTICAL, false)
        recyclerView.adapter = categoryNameAdapter
    }

    override fun onCategoryClick(adapterPosition: Int, id: Int?) {
        val intent = Intent(requireContext(), CategoryDetailActivity::class.java).apply {
            putExtra(EXTRA_MESSAGE, id)
        }
        startActivity(intent)
        dialog?.dismiss()
    }

    fun setFullScreen() {
        dialog?.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
    }
}
