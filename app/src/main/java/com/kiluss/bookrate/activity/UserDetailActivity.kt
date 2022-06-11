package com.kiluss.bookrate.activity

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.os.Bundle
import android.util.Base64
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
import com.kiluss.bookrate.utils.Constants.Companion.FOLLOWER
import com.kiluss.bookrate.utils.Constants.Companion.FOLLOWING
import okhttp3.RequestBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response
import java.text.MessageFormat

class UserDetailActivity : AppCompatActivity() {
    private lateinit var binding: ActivityUserDetailBinding
    private lateinit var followerList: ArrayList<FollowModel>
    private lateinit var followingList: ArrayList<FollowModel>
    private lateinit var api: BookService
    private lateinit var account: Account
    private lateinit var loginResponse: LoginResponse
    private var followState = false

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityUserDetailBinding.inflate(layoutInflater)
        setContentView(binding.root)
        binding.svMain.visibility = View.INVISIBLE
        loginResponse = getLoginResponse(this)
        val accountId = intent.getIntExtra(EXTRA_MESSAGE, -1)
        api = RetrofitClient.getInstance(this).getClientAuthorized(loginResponse.token.toString())
            .create(BookService::class.java)
        getAccountInfo(accountId)

        binding.tvFollowing.setOnClickListener {
            addFragmentToActivity(
                UserFollowFragment.newInstance(followerList),
                UserFollowFragment().toString()
            )
            supportActionBar?.title = FOLLOWING
        }
    }

    private fun getAccountInfo(accountId: Int) {
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
        if (account.id == loginResponse.id) {
            binding.btnFollow.visibility = View.GONE
        }
        followState = checkFollowState()
        account.myFollowers?.let { followerList = it }
        account.myFollowings?.let { followingList = it}
        binding.tvFollowed.setOnClickListener {
            addFragmentToActivity(
                UserFollowFragment.newInstance(followerList),
                UserFollowFragment().toString()
            )
            supportActionBar?.title = FOLLOWER
        }
        binding.tvFollowing.setOnClickListener {
            addFragmentToActivity(
                UserFollowFragment.newInstance(followingList),
                UserFollowFragment().toString()
            )
            supportActionBar?.title = FOLLOWING
        }
        if (followState) {
            binding.btnFollow.text = "Unfollow"
        } else {
            binding.btnFollow.text = "Follow"
        }
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
            if (followState) {
                deleteFollow()
            } else {
                postFollow()
            }
        }
    }

    private fun postFollow() {
        api.postFollow(createRequestBodyForFollow()).enqueue(object : Callback<Any?> {
            override fun onResponse(call: Call<Any?>, response: Response<Any?>) {
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
                        account.id?.let { getAccountInfo(it) }
                    }
                }
            }

            override fun onFailure(call: Call<Any?>, t: Throwable) {
                Toast.makeText(applicationContext, t.message, Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun deleteFollow() {
        api.deleteFollow(createRequestBodyForFollow()).enqueue(object : Callback<Unit> {
            override fun onResponse(call: Call<Unit>, response: Response<Unit>) {
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
                        account.id?.let { getAccountInfo(it) }
                    }
                }
            }

            override fun onFailure(call: Call<Unit>, t: Throwable) {
                Toast.makeText(applicationContext, t.message, Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun checkFollowState(): Boolean {
        if (account.myFollowers?.isEmpty() == false) {
            for (follower in account.myFollowers!!) {
                if (follower.iDFollower == loginResponse.id) return true
            }
            return false
        } else {
            return false
        }
    }

    private fun createRequestBodyForFollow() = run {
        val json = JSONObject()
        json.put("iD_Following", account.id)
        RequestBody.create(
            okhttp3.MediaType.parse("application/json; charset=utf-8"),
            json.toString()
        )
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
