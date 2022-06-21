package com.kiluss.bookrate.fragment

import android.Manifest.permission.READ_EXTERNAL_STORAGE
import android.app.Activity
import android.app.DatePickerDialog
import android.app.DatePickerDialog.OnDateSetListener
import android.content.Intent
import android.content.pm.PackageManager
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.net.Uri
import android.os.Build
import android.os.Bundle
import android.os.Environment
import android.provider.MediaStore
import android.provider.Settings
import android.text.Editable
import android.text.TextWatcher
import android.util.Base64
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.widget.PopupMenu
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import androidx.fragment.app.activityViewModels
import androidx.recyclerview.widget.GridLayoutManager
import com.kiluss.bookrate.R
import com.kiluss.bookrate.adapter.ListRequestAdapter
import com.kiluss.bookrate.data.model.Author
import com.kiluss.bookrate.data.model.BookRequest
import com.kiluss.bookrate.data.model.Publisher
import com.kiluss.bookrate.data.model.Tag
import com.kiluss.bookrate.databinding.FragmentBookRequestBinding
import com.kiluss.bookrate.network.api.BookService
import com.kiluss.bookrate.network.api.RetrofitClient
import com.kiluss.bookrate.utils.URIPathHelper
import com.kiluss.bookrate.viewmodel.MainActivityViewModel
import kotlinx.android.synthetic.main.fragment_book_request.*
import okhttp3.RequestBody
import org.json.JSONArray
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.ByteArrayOutputStream
import java.io.File
import java.util.*


class BookRequestFragment : Fragment(), ListRequestAdapter.ListRequestAdapterInterface {

    private lateinit var listCategoryDefine: ArrayList<String>
    private lateinit var listAllTag: ArrayList<Tag>
    private lateinit var listAllAuthor: ArrayList<Author>
    private var listAllAuthorName = arrayListOf<String>()
    private lateinit var listAllPublisher: ArrayList<Publisher>
    private var listAllPublisherName = arrayListOf<String>()
    private var listRequestTagId = arrayListOf<Int>()
    private var listRequestNewTag = arrayListOf<String>()
    private var requestAuthorId: Int? = null
    private var requestNewAuthor: String? = null
    private var requestPublisherId: Int? = null
    private var requestNewPublisher: String? = null
    private var requestYear: Int? = null
    private var requestPicture: String? = null
    private var requestBookName: String = "null"
    private var requestDescription: String? = null
    private var _binding: FragmentBookRequestBinding? = null
    private val binding get() = _binding!!
    private lateinit var categoryRequestAdapter: ListRequestAdapter
    private lateinit var newCategoryRequestAdapter: ListRequestAdapter
    private lateinit var listCategory: ArrayList<String>
    private lateinit var apiAuthorized: BookService
    private val activityViewModel: MainActivityViewModel by activityViewModels()

    private val pickImageFromGalleryForResult = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result: ActivityResult ->
        if (result.resultCode == Activity.RESULT_OK) {
            val intent = result.data
            val imagePath =
                intent?.data?.let { URIPathHelper().getPath(requireContext(), it) } ?: ""
            // handle image from gallery
            val file = File(imagePath)
            binding.ivCoverPicked.setImageBitmap(getFileImageBitmap(file))
            requestPicture = encodeImageToBase64String(getFileImageBitmap(file))
        }
    }

    private val requestReadPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (!isGranted) {
                Toast.makeText(
                    requireActivity(), getString(R.string.permission_denied),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

    private val requestManageStoragePermission =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {}

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        if (activityViewModel.loginResponse.value != null) {
            apiAuthorized = RetrofitClient.getInstance(requireContext()).getClientAuthorized(
                activityViewModel.loginResponse.value!!.token!!
            )
                .create(BookService::class.java)
            getAllInfo()
        } else {
            activityViewModel.loginResponse.observe(requireActivity()) {
                apiAuthorized =
                    RetrofitClient.getInstance(requireContext()).getClientAuthorized(it.token!!)
                        .create(BookService::class.java)
                getAllInfo()
            }
        }
        _binding = FragmentBookRequestBinding.inflate(inflater, container, false)
        binding.lnMain.visibility = View.INVISIBLE
        listCategoryDefine = arrayListOf()
        listCategory = arrayListOf()
        listRequestNewTag = arrayListOf()
        categoryRequestAdapter = ListRequestAdapter(listCategory, this, false)
        binding.rcvCategory.adapter = categoryRequestAdapter
        binding.rcvCategory.layoutManager = GridLayoutManager(requireContext(), 3)
        newCategoryRequestAdapter = ListRequestAdapter(listRequestNewTag, this, true)
        binding.rcvNewCategory.adapter = newCategoryRequestAdapter
        binding.rcvNewCategory.layoutManager = GridLayoutManager(requireContext(), 3)

        binding.llCategory.setOnClickListener {
            showOverflowMenuCategory(binding.llCategory, listCategoryDefine)
        }

        binding.llAuthor.setOnClickListener {
            showOverflowMenuAuthor(binding.llAuthor, listAllAuthorName)
        }

        binding.llPublisher.setOnClickListener {
            showOverflowMenuPublisher(binding.llPublisher, listAllPublisherName)
        }

        val dateSetListener =
            OnDateSetListener { _, year, _, _ ->
                binding.tvPublishDate.text = "$year"
                requestYear = year
            }
        binding.llPublishDate.setOnClickListener {
            setUpDatePicker(dateSetListener)
        }
        binding.llCover.setOnClickListener {
            val pickIntent = Intent(Intent.ACTION_PICK)
            pickIntent.setDataAndType(
                MediaStore.Images.Media.EXTERNAL_CONTENT_URI,
                "image/*"
            )
            if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.R) {
                if (Environment.isExternalStorageManager()) {
                    pickImageFromGalleryForResult.launch(pickIntent)
                } else {
                    val intent = Intent(Settings.ACTION_MANAGE_APP_ALL_FILES_ACCESS_PERMISSION)
                    intent.addCategory("android.intent.category.DEFAULT")
                    intent.data = Uri.parse(
                        String.format(
                            "package:%s",
                            requireContext().packageName
                        )
                    )
                    requestManageStoragePermission.launch(intent)
                }
            } else {
                if (ContextCompat.checkSelfPermission(
                        requireContext(),
                        READ_EXTERNAL_STORAGE
                    ) == PackageManager.PERMISSION_GRANTED
                ) {
                    pickImageFromGalleryForResult.launch(pickIntent)
                } else {
                    requestReadPermission.launch(READ_EXTERNAL_STORAGE)
                }
            }
        }
        binding.btnSubmit.setOnClickListener {
            binding.apply {
                when {
                    edtTitle.text.toString() == "" -> {
                        Toast.makeText(
                            requireContext(),
                            "Book name cannot empty!",
                            Toast.LENGTH_SHORT
                        )
                            .show()
                    }
                    tvPublishDate.text.toString() == "" -> {
                        Toast.makeText(
                            requireContext(),
                            "Publish year cannot empty!",
                            Toast.LENGTH_SHORT
                        )
                            .show()
                    }
                    tvAuthor.text.toString() == "Author" || tvAuthor.text.toString() == "" -> {
                        Toast.makeText(
                            requireContext(),
                            "Author cannot empty!",
                            Toast.LENGTH_SHORT
                        )
                            .show()
                    }
                    tvPublisher.text.toString() == "Publisher" || tvPublisher.text.toString() == "" -> {
                        Toast.makeText(
                            requireContext(),
                            "Publisher cannot empty!",
                            Toast.LENGTH_SHORT
                        )
                            .show()
                    }
                    listCategory.size == 0 -> {
                        Toast.makeText(
                            requireContext(),
                            "Category cannot empty!",
                            Toast.LENGTH_SHORT
                        )
                            .show()
                    }
                    else -> {
                        apiAuthorized.postRequestBook(createRequestBodyForBookRequest())
                            .enqueue(object : Callback<BookRequest?> {
                                override fun onResponse(
                                    call: Call<BookRequest?>,
                                    response: Response<BookRequest?>
                                ) {
                                    when {
                                        response.code() == 400 -> {
                                            Toast.makeText(
                                                context,
                                                "Bad request",
                                                Toast.LENGTH_SHORT
                                            ).show()
                                        }
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
                                                Log.e("request", response.body().toString())
                                                Toast.makeText(
                                                    context,
                                                    "Success!",
                                                    Toast.LENGTH_SHORT
                                                ).show()
                                            }
                                        }
                                    }
                                    binding.apply {
                                        edtTitle.setText("")
                                        edtAuthor.setText("")
                                        edtDescription.setText("")
                                        edtPublisher.setText("")
                                        tvPublishDate.text = ""
                                        tvAuthor.text = ""
                                        tvPublisher.text = ""
                                        ivCoverPicked.setImageDrawable(ContextCompat.getDrawable(requireContext(), R.drawable.book_cover_default))
                                        resetListCategory()
                                        edtTitle.requestFocus()
                                    }
                                }

                                override fun onFailure(call: Call<BookRequest?>, t: Throwable) {
                                    Toast.makeText(
                                        context,
                                        t.message,
                                        Toast.LENGTH_SHORT
                                    ).show()
                                }
                            })
                    }
                }
            }
        }

        binding.edtTitle.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                requestBookName = edtTitle.text.toString()
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        binding.edtDescription.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                requestDescription = edtDescription.text.toString()
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        binding.edtAuthor.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                binding.tvAuthor.text = edtAuthor.text.toString()
                requestNewAuthor = edtAuthor.text.toString()
                requestAuthorId = null
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        binding.edtPublisher.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                binding.tvPublisher.text = edtPublisher.text.toString()
                requestNewPublisher = edtPublisher.text.toString()
                requestPublisherId = null
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        binding.edtAuthor.addTextChangedListener(object : TextWatcher {
            override fun afterTextChanged(s: Editable?) {
                binding.tvAuthor.text = edtAuthor.text.toString()
                requestNewAuthor = edtAuthor.text.toString()
                requestAuthorId = null
            }

            override fun beforeTextChanged(p0: CharSequence?, p1: Int, p2: Int, p3: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {}
        })

        binding.ivSendNewCategory.setOnClickListener {
            if (binding.edtRequestCategory.text.toString() != "") {
                listRequestNewTag.add(binding.edtRequestCategory.text.toString())
                binding.edtRequestCategory.setText("")
                newCategoryRequestAdapter.notifyItemInserted(listRequestNewTag.size)
            }
        }

        return binding.root
    }

    private fun getAllInfo() {
        apiAuthorized.getAllTag().enqueue(object : Callback<ArrayList<Tag>?> {
            override fun onResponse(
                call: Call<ArrayList<Tag>?>,
                response: Response<ArrayList<Tag>?>
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
                            listAllTag = response.body()!!
                            listAllTag.forEach {
                                it.name?.let { it1 -> listCategoryDefine.add(it1) }
                            }
                            listCategoryDefine.sort()
                        }
                    }
                }
                if (_binding != null) {
                    binding.lnMain.visibility = View.VISIBLE
                    binding.pbLoading.visibility = View.GONE
                }
            }

            override fun onFailure(call: Call<ArrayList<Tag>?>, t: Throwable) {
                Toast.makeText(
                    context,
                    t.message,
                    Toast.LENGTH_SHORT
                ).show()
                if (_binding != null) {
                    binding.lnMain.visibility = View.VISIBLE
                    binding.pbLoading.visibility = View.GONE
                }
            }
        })

        apiAuthorized.getAllAuthor().enqueue(object : Callback<ArrayList<Author>?> {
            override fun onResponse(
                call: Call<ArrayList<Author>?>,
                response: Response<ArrayList<Author>?>
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
                            listAllAuthor = response.body()!!
                            listAllAuthor.forEach {
                                it.stageName?.let { it1 -> listAllAuthorName.add(it1) }
                            }
                        }
                    }
                }
            }

            override fun onFailure(call: Call<ArrayList<Author>?>, t: Throwable) {
                Toast.makeText(
                    context,
                    t.message,
                    Toast.LENGTH_SHORT
                ).show()
            }
        })

        apiAuthorized.getAllPublisher().enqueue(object : Callback<ArrayList<Publisher>?> {
            override fun onResponse(
                call: Call<ArrayList<Publisher>?>,
                response: Response<ArrayList<Publisher>?>
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
                            listAllPublisher = response.body()!!
                            listAllPublisher.forEach {
                                it.name?.let { it1 -> listAllPublisherName.add(it1) }
                            }
                            Log.e("publisher", response.body().toString())
                        }
                    }
                }
            }

            override fun onFailure(call: Call<ArrayList<Publisher>?>, t: Throwable) {
                Toast.makeText(
                    context,
                    t.message,
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
    }

    private fun createRequestBodyForBookRequest() = run {
        val json = JSONObject()
        json.put("bookName", requestBookName)
        Log.e("author", requestAuthorId.toString())
        Log.e("newAuthor", requestNewAuthor.toString())
        if (requestNewAuthor != "") {
            json.put("iD_Aut", JSONObject.NULL)
        } else {
            json.put("iD_Aut", requestAuthorId)
        }
        if (requestNewPublisher != "") {
            json.put("iD_Pub", JSONObject.NULL)
        } else {
            json.put("iD_Pub", requestPublisherId)
        }
        if (requestYear == null) {
            json.put("publishedYear", JSONObject.NULL)
        } else {
            json.put("publishedYear", requestYear)
        }
        if (requestPicture == null) {
            json.put("picture", JSONObject.NULL)
        } else {
            json.put("picture", requestPicture)
        }
        if (requestDescription == null) {
            json.put("description", "")
        } else {
            json.put("description", requestDescription)
        }
        if (requestNewAuthor == "") {
            json.put("newAut", "")
        } else {
            json.put("newAut", requestNewAuthor)
        }
        if (requestNewPublisher == "") {
            json.put("newPub", "")
        } else {
            json.put("newPub", requestNewPublisher)
        }
        val listRequestTagIdJsonArray = JSONArray(listRequestTagId)
        json.put("list_ID_Tags", listRequestTagIdJsonArray)
//        val listRequestNewTagJsonArray = JSONArray(listRequestNewTag)
//        json.put("list_new_tags", listRequestNewTagJsonArray)
        Log.e("object", json.toString())
        RequestBody.create(
            okhttp3.MediaType.parse("application/json; charset=utf-8"),
            json.toString()
        )
    }

    private fun encodeImageToBase64String(bm: Bitmap): String {
        val baos = ByteArrayOutputStream()
        bm.compress(Bitmap.CompressFormat.JPEG, 50, baos)
        val byteArray = baos.toByteArray()
        var base64Image = Base64.encodeToString(byteArray, Base64.DEFAULT).trim()
        base64Image = base64Image.replace(" ", "")
        base64Image = base64Image.lines().joinToString("")
        return base64Image
    }

    private fun setUpDatePicker(dateSetListener: OnDateSetListener?) {
        val year = Calendar.getInstance().get(Calendar.YEAR)
        val month = Calendar.getInstance().get(Calendar.MONTH)
        val day = Calendar.getInstance().get(Calendar.DATE)
        val datePickerDialog = DatePickerDialog(
            requireContext(),
            dateSetListener, year, month, day
        )
        datePickerDialog.show()
    }

    private fun showOverflowMenuCategory(anchor: View, listCate: ArrayList<String>) {
        val menu = PopupMenu(requireContext(), anchor)
        menu.menu.apply {
            for (category in listCate) {
                add(category).setOnMenuItemClickListener {
                    listCategory.add(category)
                    listAllTag.forEach {
                        if (category == it.name) {
                            it.id?.let { it1 -> listRequestTagId.add(it1) }
                        }
                    }
                    listCate.remove(category)
                    categoryRequestAdapter.notifyItemInserted(listCategory.size)
                    true
                }
            }
        }
        menu.show()
    }

    private fun showOverflowMenuAuthor(anchor: View, list: ArrayList<String>) {
        val menu = PopupMenu(requireContext(), anchor)
        menu.menu.apply {
            for (author in list) {
                add(author).setOnMenuItemClickListener {
                    binding.edtAuthor.setText("")
                    binding.edtAuthor.clearFocus()
                    listAllAuthor.forEach {
                        if (author == it.stageName) {
                            it.id?.let { it1 -> requestAuthorId = it1 }
                        }
                    }
                    binding.tvAuthor.text = author
                    true
                }
            }
        }
        menu.show()
    }

    private fun showOverflowMenuPublisher(anchor: View, list: ArrayList<String>) {
        val menu = PopupMenu(requireContext(), anchor)
        menu.menu.apply {
            for (pub in list) {
                add(pub).setOnMenuItemClickListener {
                    binding.edtPublisher.setText("")
                    binding.edtPublisher.clearFocus()
                    listAllPublisher.forEach {
                        if (pub == it.name) {
                            it.id?.let { it1 -> requestPublisherId = it1 }
                        }
                    }
                    binding.tvPublisher.text = pub
                    true
                }
            }
        }
        menu.show()
    }

    private fun getFileImageBitmap(imgFile: File): Bitmap {
        return BitmapFactory.decodeFile(imgFile.absolutePath)
    }

    private fun resetListCategory() {
        apiAuthorized.getAllTag().enqueue(object : Callback<ArrayList<Tag>?> {
            override fun onResponse(
                call: Call<ArrayList<Tag>?>,
                response: Response<ArrayList<Tag>?>
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
                            listAllTag = response.body()!!
                            listCategoryDefine.clear()
                            listAllTag.forEach {
                                it.name?.let { it1 -> listCategoryDefine.add(it1) }
                            }
                        }
                    }
                }
            }

            override fun onFailure(call: Call<ArrayList<Tag>?>, t: Throwable) {
                Toast.makeText(
                    context,
                    t.message,
                    Toast.LENGTH_SHORT
                ).show()
            }
        })
        listCategory.clear()
        listCategoryDefine.sort()
        categoryRequestAdapter.notifyDataSetChanged()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onClearItemClick(adapterPosition: Int, category: String) {
        listCategory.removeAt(adapterPosition)
        listCategoryDefine.add(category)
        listCategoryDefine.sort()
        categoryRequestAdapter.notifyItemRemoved(adapterPosition)
    }

    override fun onClearNewItemClick(adapterPosition: Int, category: String) {
        listRequestNewTag.removeAt(adapterPosition)
        newCategoryRequestAdapter.notifyItemRemoved(adapterPosition)
    }
}
