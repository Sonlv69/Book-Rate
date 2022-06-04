package com.kiluss.bookrate.activity

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
import android.util.Log
import android.view.View
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.google.gson.Gson
import com.kiluss.bookrate.R
import com.kiluss.bookrate.data.model.Account
import com.kiluss.bookrate.data.model.FollowModel
import com.kiluss.bookrate.data.model.LoginResponse
import com.kiluss.bookrate.databinding.ActivityUserDetailBinding
import com.kiluss.bookrate.fragment.UserFollowFragment
import com.kiluss.bookrate.network.api.BookService
import com.kiluss.bookrate.network.api.RetrofitClient
import com.kiluss.bookrate.utils.Constants.Companion.EXTRA_MESSAGE
import com.kiluss.bookrate.utils.Constants.Companion.FOLLOWED
import com.kiluss.bookrate.utils.Constants.Companion.FOLLOWING
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.MessageFormat


class UserDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityUserDetailBinding
    private lateinit var followList: List<FollowModel>
    private lateinit var api: BookService
    private lateinit var account: Account
    private lateinit var loginResponse: LoginResponse

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.svMain.visibility = View.INVISIBLE
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
        loginResponse = getLoginResponse(this)
        val accountId = intent.getIntExtra(EXTRA_MESSAGE, -1)
        setUpApi(accountId)

        binding.tvFollowing.setOnClickListener {
            addFragmentToActivity(
                UserFollowFragment.newInstance(followList as ArrayList<FollowModel>),
                UserFollowFragment().toString()
            )
            supportActionBar?.title = FOLLOWING
        }
    }

    private fun setUpApi(accountId: Int) {
        api = RetrofitClient.getInstance(this).getClientAuthorized(loginResponse.token.toString())
            .create(BookService::class.java)
        api.getAccountInfo(accountId.toString())
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
                        response.code() == 500 -> {
                            Toast.makeText(
                                applicationContext,
                                "Internal error",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        response.isSuccessful -> {
                            response.body()?.userName?.let { Log.e("id", it) }
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
        binding.tvDisplayName.text = info.userName
        info.fullName?.let { binding.tvFullName.text = info.fullName }
        info.address?.let { binding.tvAddress.text = info.address }
        info.birthday?.let { binding.tvBirthDay.text = convertDateTime(info.birthday.toString()) }
        info.picture?.let {
            if (it != "") {
                binding.ivProfile.setImageBitmap(base64ToBitmapDecode(info.picture.toString()))
            }
        }
        binding.tvFollowing.text = MessageFormat.format(
            resources.getText(R.string.text_following).toString(),
            info.myFollowings?.size
        )
        binding.tvFollowed.text = MessageFormat.format(
            resources.getText(R.string.text_follower).toString(),
            info.myFollowers?.size
        )
        binding.btnFollow.setOnClickListener {

        }
    }

    private fun getLoginResponse(context: Context) : LoginResponse {
        val sharedPref = context.getSharedPreferences(
            context.getString(R.string.saved_login_account_key),
            Context.MODE_PRIVATE
        )
        val gson = Gson()
        val json: String? = sharedPref.getString(context.getString(R.string.saved_login_account_key), "")
        return gson.fromJson(json, LoginResponse::class.java)
    }

    private fun convertDateTime(jsonDate: String): String {
        return jsonDate.split("T")[0]
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