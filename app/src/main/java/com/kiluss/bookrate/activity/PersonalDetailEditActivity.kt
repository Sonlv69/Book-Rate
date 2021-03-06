package com.kiluss.bookrate.activity

import android.Manifest
import android.app.Activity
import android.app.DatePickerDialog
import android.content.Context
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
import android.util.Base64
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.activity.result.ActivityResult
import androidx.activity.result.contract.ActivityResultContracts
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.google.gson.Gson
import com.kiluss.bookrate.R
import com.kiluss.bookrate.data.model.Account
import com.kiluss.bookrate.data.model.LoginResponse
import com.kiluss.bookrate.databinding.ActivityPersonalDetailEditBinding
import com.kiluss.bookrate.network.api.BookService
import com.kiluss.bookrate.network.api.RetrofitClient
import com.kiluss.bookrate.utils.Constants.Companion.FORMAT_DATE_ISO1
import com.kiluss.bookrate.utils.URIPathHelper
import okhttp3.RequestBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.io.ByteArrayOutputStream
import java.io.File
import java.text.SimpleDateFormat
import java.util.*


class PersonalDetailEditActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPersonalDetailEditBinding
    private var account: Account = Account()
    private lateinit var api: BookService
    private val pickImageFromGalleryForResult = registerForActivityResult(
        ActivityResultContracts.StartActivityForResult()
    ) { result: ActivityResult ->
        if (result.resultCode == Activity.RESULT_OK) {
            val intent = result.data
            val imagePath =
                intent?.data?.let { URIPathHelper().getPath(this, it) } ?: ""
            // handle image from gallery
            val file = File(imagePath)
            val imageBitmap = getFileImageBitmap(file)
            binding.ivProfile.setImageBitmap(imageBitmap)
            account.picture = encodeImageToBase64String(imageBitmap)
        }
    }

    private val requestReadPermission =
        registerForActivityResult(ActivityResultContracts.RequestPermission()) { isGranted ->
            if (!isGranted) {
                Toast.makeText(
                    this, getString(R.string.permission_denied),
                    Toast.LENGTH_SHORT
                ).show()
            }
        }

    private val requestManageStoragePermission =
        registerForActivityResult(ActivityResultContracts.StartActivityForResult()) {}

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPersonalDetailEditBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.svMain.visibility = View.INVISIBLE
        val sharedPref = getSharedPreferences(
            getString(R.string.saved_login_account_key),
            Context.MODE_PRIVATE
        )
        val gson = Gson()
        val json: String? = sharedPref.getString(getString(R.string.saved_login_account_key), "")
        val loginResponse = gson.fromJson(json, LoginResponse::class.java)
        api = RetrofitClient.getInstance(this).getClientAuthorized(loginResponse.token.toString())
            .create(BookService::class.java)
        setUpApi(loginResponse.id.toString())
        val dateSetListener =
            DatePickerDialog.OnDateSetListener { _, year, monthOfYear, dayOfMonth ->
                binding.tvBirthDayPicker.text = "$year-${monthOfYear+1}-$dayOfMonth"
                account.birthday = timeToIsoString(year, monthOfYear, dayOfMonth)
            }

        binding.tvBirthDayPicker.setOnClickListener {
            setUpDatePicker(dateSetListener)
        }
        binding.ivProfile.setOnClickListener {
            pickImage()
        }
        binding.btnSave.setOnClickListener {
            account.address = binding.edtAddress.text.toString()
            account.fullName = binding.edtFullName.text.toString()
            binding.btnSave.isClickable = false
            uploadChange(loginResponse.id.toString())
        }
    }

    private fun uploadChange(id : String) {
        binding.pbChangeLoading.visibility = View.VISIBLE
        api.changeMyAccountInfo(
                id,
                createRequestBody()
            )
            .enqueue(object : Callback<Account?> {
                override fun onResponse(
                    call: Call<Account?>,
                    response: Response<Account?>
                ) {
                    when {
                        response.code() == 404 -> {
                            Toast.makeText(
                                applicationContext,
                                "Url is not exist",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        response.code() == 401 -> {
                            Toast.makeText(
                                applicationContext,
                                "Token unauthorized",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        response.isSuccessful -> {
                            Log.e("TAG", response.body().toString())
                            finish()
                        }
                    }
                }

                override fun onFailure(call: Call<Account?>, t: Throwable) {
                    Log.e("TAG", t.stackTraceToString())
                    Toast.makeText(applicationContext, t.message, Toast.LENGTH_LONG).show()
                    binding.pbChangeLoading.visibility = View.GONE
                    binding.btnSave.isClickable = true
                }
            })
    }

    private fun pickImage() {
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
                        this.packageName
                    )
                )
                requestManageStoragePermission.launch(intent)
            }
        } else {
            if (ContextCompat.checkSelfPermission(
                    this,
                    Manifest.permission.READ_EXTERNAL_STORAGE
                ) == PackageManager.PERMISSION_GRANTED
            ) {
                pickImageFromGalleryForResult.launch(pickIntent)
            } else {
                requestReadPermission.launch(Manifest.permission.READ_EXTERNAL_STORAGE)
            }
        }
    }

    private fun timeToIsoString(year: Int, month: Int, day: Int): String {
        val format = SimpleDateFormat(FORMAT_DATE_ISO1)
        val calendar = Calendar.getInstance()
        calendar.set(year, month, day)
        return format.format(calendar.time)
    }

    private fun createRequestBody() = RequestBody.create(
        okhttp3.MediaType.parse("application/json; charset=utf-8"),
        createJsonObject().toString()
    )

    private fun createJsonObject(): JSONObject {
        val json = JSONObject()
        Log.e("picture in account", account.picture.toString())
        json.put("isActive", true)
        json.put("fullName", account.fullName)
        json.put("birthday", account.birthday)
        json.put("address", account.address)
        json.put("picture", account.picture)
        return json
    }

    private fun setUpApi(id: String) {
        api.getAccountInfo(id)
            .enqueue(object : Callback<Account?> {
                override fun onResponse(
                    call: Call<Account?>,
                    response: Response<Account?>
                ) {
                    when {
                        response.code() == 404 -> {
                            Toast.makeText(
                                applicationContext,
                                "Url is not exist",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        response.code() == 401 -> {
                            Toast.makeText(
                                applicationContext,
                                "Token unauthorized",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        response.isSuccessful -> {
                            binding.svMain.visibility = View.VISIBLE
                            account = response.body()!!
                            updateUi(account)
                        }
                    }
                    binding.pbLoading.visibility = View.GONE
                }

                override fun onFailure(call: Call<Account?>, t: Throwable) {
                    Toast.makeText(applicationContext, t.message, Toast.LENGTH_LONG).show()
                }
            })
    }

    private fun updateUi(info: Account) {
        binding.tvDisplayName.setText(info.userName)
        info.fullName?.let { binding.edtFullName.setText(info.fullName) }
        info.address?.let { binding.edtAddress.setText(info.address) }
        info.birthday?.let { binding.tvBirthDayPicker.text = convertDateTime(info.birthday.toString()) }
        info.picture?.let {
            if (it != "") {
                binding.ivProfile.setImageBitmap(base64ToBitmapDecode(info.picture.toString()))
            }
        }
    }

    private fun setUpDatePicker(dateSetListener: DatePickerDialog.OnDateSetListener?) {
        var year = Calendar.getInstance().get(Calendar.YEAR)
        var month = Calendar.getInstance().get(Calendar.MONTH)
        var day = Calendar.getInstance().get(Calendar.DATE)
        val datePickerDialog = DatePickerDialog(
            this,
            dateSetListener, year, month, day
        )
        datePickerDialog.show()
    }

    private fun getFileImageBitmap(imgFile: File): Bitmap {
        return BitmapFactory.decodeFile(imgFile.absolutePath)
    }

    private fun base64ToBitmapDecode(base64Image: String): Bitmap? {
        val decodedString = Base64.decode(base64Image, Base64.DEFAULT)
        return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
    }

    private fun convertDateTime(jsonDate: String): String {
        return jsonDate.split("T")[0]
    }

    private fun encodeImageToBase64String(bm: Bitmap): String? {
        val baos = ByteArrayOutputStream()
        bm.compress(Bitmap.CompressFormat.JPEG, 50, baos)
        val byteArray = baos.toByteArray()
        var base64Image = Base64.encodeToString(byteArray, Base64.DEFAULT).trim()
        base64Image = base64Image.replace(" ", "")
        base64Image = base64Image.lines().joinToString("")
        return base64Image
    }
}