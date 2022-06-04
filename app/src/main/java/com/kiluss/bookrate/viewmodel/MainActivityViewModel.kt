package com.kiluss.bookrate.viewmodel

import android.content.Context
import android.graphics.Bitmap
import android.graphics.BitmapFactory
import android.util.Base64
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.ViewModel
import androidx.lifecycle.ViewModelProvider
import com.google.android.material.badge.BadgeDrawable
import com.google.android.material.bottomnavigation.BottomNavigationView
import com.google.gson.Gson
import com.kiluss.bookrate.R
import com.kiluss.bookrate.data.model.Account
import com.kiluss.bookrate.data.model.LoginResponse
import com.kiluss.bookrate.network.api.BookService
import com.kiluss.bookrate.network.api.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class MainActivityViewModel(context: Context) : ViewModel() {
    class ViewModelFactory(private val context: Context) : ViewModelProvider.Factory {
        override fun <T : ViewModel> create(modelClass: Class<T>): T {
            if (modelClass.isAssignableFrom(MainActivityViewModel::class.java)) {
                return MainActivityViewModel(context) as T
            }
            throw IllegalArgumentException("Unknown ViewModel class")
        }
    }
    private val _notification: MutableLiveData<String> by lazy { MutableLiveData<String>() }
    internal val notification: LiveData<String> = _notification
    private val _accountInfo: MutableLiveData<Account> by lazy { MutableLiveData<Account>() }
    internal val accountInfo: LiveData<Account> = _accountInfo
    private val _loginResponse: MutableLiveData<LoginResponse> by lazy { MutableLiveData<LoginResponse>() }
    internal val loginResponse: LiveData<LoginResponse> = _loginResponse
    private lateinit var api: BookService

    init {
        _loginResponse.value = getLoginResponse(context)
        getMyAccount(context)
    }

    internal fun setNotification(context: Context, numberNotify: Int) {
        val navBottomView = (context as AppCompatActivity).findViewById<BottomNavigationView>(R.id.bottomNavBar)
        val badgeDrawable: BadgeDrawable? = navBottomView.getBadge(R.id.accountFollowingFragment)
        if (badgeDrawable == null) {
            navBottomView.getOrCreateBadge(R.id.accountFollowingFragment).number = numberNotify
        } else {
            val previousValue = badgeDrawable.number
            badgeDrawable.number = numberNotify
        }
    }

    internal fun base64ToBitmapDecode(base64Image: String): Bitmap? {
        val decodedString = Base64.decode(base64Image, Base64.DEFAULT)
        return BitmapFactory.decodeByteArray(decodedString, 0, decodedString.size)
    }

    internal fun getMyAccount(context: Context) {
        api = RetrofitClient.getInstance(context).getClientAuthorized(loginResponse.value?.token.toString())
            .create(BookService::class.java)
        api.getAccountInfo(loginResponse.value?.id.toString())
            .enqueue(object : Callback<Account?> {
                override fun onResponse(
                    call: Call<Account?>,
                    response: Response<Account?>
                ) {
                    when {
                        response.code() == 404 -> {
                            Toast.makeText(
                                context,
                                "Url is not exist",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        response.isSuccessful -> {
                            _accountInfo.value = response.body()
                        }
                    }
                }

                override fun onFailure(call: Call<Account?>, t: Throwable) {
                    Toast.makeText(context, t.message, Toast.LENGTH_LONG).show()
                }
            })
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
}