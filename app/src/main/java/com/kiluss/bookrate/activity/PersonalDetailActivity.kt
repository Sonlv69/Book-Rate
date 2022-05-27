package com.kiluss.bookrate.activity

import android.content.Context
import android.content.Intent
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import androidx.fragment.app.Fragment
import com.google.gson.Gson
import com.kiluss.bookrate.R
import com.kiluss.bookrate.data.model.FollowModel
import com.kiluss.bookrate.data.model.LoginResponse
import com.kiluss.bookrate.data.model.MyAccountInfo
import com.kiluss.bookrate.databinding.ActivityPersonalDetailBinding
import com.kiluss.bookrate.fragment.UserFollowFragment
import com.kiluss.bookrate.network.api.BookService
import com.kiluss.bookrate.network.api.RetrofitClient
import com.kiluss.bookrate.utils.Const.Companion.FOLLOWED
import com.kiluss.bookrate.utils.Const.Companion.FOLLOWING
import com.kiluss.bookrate.utils.Const.Companion.FORMAT_DATE_ISO
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.SimpleDateFormat
import java.util.*
import kotlin.collections.ArrayList


class PersonalDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityPersonalDetailBinding
    private lateinit var followList: List<FollowModel>
    private lateinit var api: BookService
    private lateinit var accountInfo: MyAccountInfo

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityPersonalDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.svMain.visibility = View.INVISIBLE
        setUpApi()
        followList = arrayListOf(
            FollowModel("", "KiluSs", 12, true),
            FollowModel("", "KiluSs", 12, false),
            FollowModel("", "KiluSs", 12, true),
            FollowModel("", "KiluSs", 12, false),
            FollowModel("", "KiluSs", 12, true),
            FollowModel("", "KiluSs", 12, false),
        )
        binding.tvFollowed.setOnClickListener {
            addFragmentToActivity(
                UserFollowFragment.newInstance(followList as ArrayList<FollowModel>),
                UserFollowFragment().toString()
            )
            supportActionBar?.title = FOLLOWED
        }

        binding.tvFollowing.setOnClickListener {
            addFragmentToActivity(
                UserFollowFragment.newInstance(followList as ArrayList<FollowModel>),
                UserFollowFragment().toString()
            )
            supportActionBar?.title = FOLLOWING
        }
    }

    private fun setUpApi() {
        val sharedPref = getSharedPreferences(
            getString(R.string.saved_login_account_key),
            Context.MODE_PRIVATE
        )
        val gson = Gson()
        val json: String? = sharedPref.getString(getString(R.string.saved_login_account_key), "")
        val loginResponse = gson.fromJson(json, LoginResponse::class.java)
        api = RetrofitClient.getInstance(this).getClientAuthorized(loginResponse.token.toString())
            .create(BookService::class.java)
        api.getMyAccountInfo(loginResponse.id.toString())
            .enqueue(object : Callback<MyAccountInfo?> {
                override fun onResponse(
                    call: Call<MyAccountInfo?>,
                    response: Response<MyAccountInfo?>
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
                            accountInfo = response.body()!!
                            Log.e("TAG", "onResponse: " + accountInfo.toString())
                            updateUi(accountInfo)
                        }
                    }
                    binding.pbLoading.visibility = View.GONE
                }

                override fun onFailure(call: Call<MyAccountInfo?>, t: Throwable) {
                    Toast.makeText(applicationContext, t.message, Toast.LENGTH_LONG).show()
                }
            })
    }

    private fun updateUi(info: MyAccountInfo) {
        binding.edtDisplayName.text = info.userName
        info.fullName?.let { binding.tvFullName.text = info.fullName }
        info.address?.let { binding.tvAddress.text = info.address }
        info.birthday?.let { binding.tvBirthDay.text = convertDateTime(info.birthday.toString()) }
        if (info.isActive == true) {
            binding.ivActiveState.setColorFilter(
                ContextCompat.getColor(this, R.color.green),
                android.graphics.PorterDuff.Mode.SRC_IN
            )
        } else {
            binding.ivActiveState.setColorFilter(
                ContextCompat.getColor(this, R.color.lightGray),
                android.graphics.PorterDuff.Mode.SRC_IN
            )
        }
        info.picture?.let { binding.ivProfile.setImageBitmap(base64ToBitmapDecode(info.picture.toString())) }
    }

    private fun convertDateTime(jsonDate: String): String {
        val myFormat = SimpleDateFormat(FORMAT_DATE_ISO)
        val myDate = myFormat.parse(jsonDate)
        val cal: Calendar = Calendar.getInstance()
        cal.time = myDate
        val year: Int = cal.get(Calendar.YEAR)
        val month: Int = cal.get(Calendar.MONTH) + 1
        val day: Int = cal.get(Calendar.DAY_OF_MONTH)
        return "$day/$month/$year"
    }

    private fun addFragmentToActivity(fragment: Fragment?, name: String) {
        if (fragment == null) return
        val fm = supportFragmentManager
        val tr = fm.beginTransaction()
        tr.add(R.id.root_container, fragment, name)
        tr.addToBackStack(name)
        tr.commitAllowingStateLoss()
    }

    private fun base64ToBitmapDecode(base64Image: String): Bitmap? {
        val decodedString = Base64.decode(base64Image, Base64.DEFAULT)
        return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
    }
}