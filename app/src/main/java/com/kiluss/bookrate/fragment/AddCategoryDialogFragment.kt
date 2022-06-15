package com.kiluss.bookrate.fragment

import android.content.Context
import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.fragment.app.DialogFragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.google.gson.Gson
import com.kiluss.bookrate.R
import com.kiluss.bookrate.activity.CategoryDetailActivity
import com.kiluss.bookrate.adapter.AddCategoryAdapter
import com.kiluss.bookrate.adapter.CategoryNameAdapter
import com.kiluss.bookrate.data.model.*
import com.kiluss.bookrate.network.api.BookService
import com.kiluss.bookrate.network.api.RetrofitClient
import com.kiluss.bookrate.utils.Constants.Companion.EXTRA_MESSAGE
import com.kiluss.bookrate.viewmodel.MainActivityViewModel
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class AddCategoryDialogFragment() : DialogFragment(), AddCategoryAdapter.CategoryNameAdapterInterface {
    private lateinit var categoryNameAdapter: AddCategoryAdapter
    private lateinit var recyclerView: RecyclerView
    private var allCategory: ArrayList<Tag> = arrayListOf()
    private var myCategory: ArrayList<MyTags> = arrayListOf()
    private val viewModel: MainActivityViewModel by activityViewModels()

    companion object {
        @JvmStatic
        fun newInstance(param1: ArrayList<Tag>, param2: ArrayList<MyTags>) =
            AddCategoryDialogFragment().apply {
                arguments = Bundle().apply {
                }
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
        setFullScreen()
    }

    private fun setupView() {
        categoryNameAdapter = AddCategoryAdapter(allCategory, myCategory, this)
        recyclerView.layoutManager =
            LinearLayoutManager(context, LinearLayoutManager.VERTICAL, false)
        recyclerView.adapter = categoryNameAdapter
    }

    override fun onCategoryClick(adapterPosition: Int, id: Int?) {
        startCategoryActivity(id)
    }

    override fun onPostMyTagClick(adapterPosition: Int, id: Int?) {
        postTag(id)
        viewModel.reloadTagData()
    }

    override fun onDeleteMyTagClick(adapterPosition: Int, id: Int?) {
        deleteTag(id)
        viewModel.reloadTagData()
    }

    private fun getMyAccountInfo() {
        RetrofitClient.getInstance(requireContext())
            .getClientAuthorized(getLoginResponse().token!!)
            .create(BookService::class.java).getMyAccountInfo().enqueue(object : Callback<AccountHasMytag> {
            override fun onResponse(
                call: Call<AccountHasMytag>,
                response: Response<AccountHasMytag>
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
                            myCategory.clear()
                            myCategory.addAll(it.myTags)
                            setupView()
                        }
                    }
                }
            }

            override fun onFailure(call: Call<AccountHasMytag>, t: Throwable) {

            }
        })
    }

    private fun getAllTag() {
        RetrofitClient.getInstance(requireContext())
            .getClientAuthorized(getLoginResponse().token!!)
            .create(BookService::class.java).getAllTag().enqueue(object : Callback<java.util.ArrayList<Tag>?> {
            override fun onResponse(
                call: Call<java.util.ArrayList<Tag>?>,
                response: Response<java.util.ArrayList<Tag>?>
            ) {
                when {
                    response.code() == 404 -> {
                        Toast.makeText(
                            context,
                            "Url is not exist",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    response.code() == 500 -> {
                        Toast.makeText(
                            context,
                            "Internal error",
                            Toast.LENGTH_SHORT
                        ).show()
                    }
                    response.isSuccessful -> {
                        response.body()?.let {
                            allCategory.clear()
                            allCategory.addAll(it)
                            if (::categoryNameAdapter.isInitialized) {
                                categoryNameAdapter.notifyDataSetChanged()
                            }
                        }
                    }
                }
            }

            override fun onFailure(call: Call<java.util.ArrayList<Tag>?>, t: Throwable) {
            }
        })
    }

    private fun postTag(id: Int?) {
        id?.let {
            RetrofitClient.getInstance(requireContext())
                .getClientAuthorized(getLoginResponse().token!!)
                .create(BookService::class.java).postMyTag(it).enqueue(object : Callback<Any> {
                override fun onResponse(
                    call: Call<Any>,
                    response: Response<Any>
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
                    }
                }

                override fun onFailure(call: Call<Any>, t: Throwable) {
                }
            })
        }
    }

    private fun deleteTag(id: Int?) {
        id?.let {
            RetrofitClient.getInstance(requireContext())
                .getClientAuthorized(getLoginResponse().token!!)
                .create(BookService::class.java).deleteMyTag(it).enqueue(object : Callback<Unit> {
                    override fun onResponse(
                        call: Call<Unit>,
                        response: Response<Unit>
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
                        }
                    }

                    override fun onFailure(call: Call<Unit>, t: Throwable) {
                    }
                })
        }
    }

    override fun onResume() {
        super.onResume()
        getMyAccountInfo()
        getAllTag()
    }


    private fun startCategoryActivity(id: Int?) {
        val intent = Intent(requireContext(), CategoryDetailActivity::class.java).apply {
            putExtra(EXTRA_MESSAGE, id)
        }
        startActivity(intent)
        dialog?.dismiss()
    }

    private fun setFullScreen() {
        dialog?.window?.setLayout(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT)
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
}
