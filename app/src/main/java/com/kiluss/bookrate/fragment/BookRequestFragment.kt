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
import androidx.recyclerview.widget.GridLayoutManager
import com.kiluss.bookrate.R
import com.kiluss.bookrate.adapter.CategoryRequestAdapter
import com.kiluss.bookrate.databinding.FragmentBookRequestBinding
import com.kiluss.bookrate.utils.URIPathHelper
import java.io.File
import java.util.*
import kotlin.collections.ArrayList


class BookRequestFragment : Fragment(), CategoryRequestAdapter.CategoryRequestAdapterInterface {

    private lateinit var listCategoryDefine: ArrayList<String>
    private var _binding: FragmentBookRequestBinding? = null
    private val binding get() = _binding!!
    private lateinit var categoryRequestAdapter: CategoryRequestAdapter
    private lateinit var listCategory: ArrayList<String>
    private val pickImageFromGalleryForResult = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result: ActivityResult ->
        if (result.resultCode == Activity.RESULT_OK) {
            val intent = result.data
            val imagePath =
                intent?.data?.let { URIPathHelper().getPath(requireContext(), it) } ?: ""
            // handle image from gallery
            val file = File(imagePath)
            Log.e("result", file.absolutePath)
            binding.ivCoverPicked.setImageBitmap(getFileImageBitmap(file))
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
        _binding = FragmentBookRequestBinding.inflate(inflater, container, false)

        listCategoryDefine =
            arrayListOf("Fiction", "History", "Science", "Detective", "Fantasy", "Romance")
        listCategory = arrayListOf()
        categoryRequestAdapter = CategoryRequestAdapter(listCategory, this)
        binding.rcvCategory.adapter = categoryRequestAdapter
        binding.rcvCategory.layoutManager = GridLayoutManager(requireContext(), 3)

        binding.llCategory.setOnClickListener {
            showOverflowMenuCategory(binding.llCategory, listCategoryDefine)
        }
        val dateSetListener =
            OnDateSetListener { view, year, monthOfYear, dayOfMonth ->
                val month = monthOfYear + 1
                binding.tvPublishDate.text = "$dayOfMonth/$month/$year"
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

        return binding.root
    }

    private fun setUpDatePicker(dateSetListener: OnDateSetListener?) {
        var year = Calendar.getInstance().get(Calendar.YEAR)
        var month = Calendar.getInstance().get(Calendar.MONTH)
        var day = Calendar.getInstance().get(Calendar.DATE)
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
                    listCate.remove(category)
                    categoryRequestAdapter.notifyItemInserted(listCategory.size)
                    true
                }
            }
        }
        menu.show()
    }

    private fun getFileImageBitmap(imgFile: File): Bitmap {
        return BitmapFactory.decodeFile(imgFile.absolutePath)
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }

    override fun onClearCategoryClick(adapterPosition: Int, category: String) {
        listCategory.removeAt(adapterPosition)
        listCategoryDefine.add(category)
        listCategoryDefine.sort()
        categoryRequestAdapter.notifyItemRemoved(adapterPosition)
    }
}