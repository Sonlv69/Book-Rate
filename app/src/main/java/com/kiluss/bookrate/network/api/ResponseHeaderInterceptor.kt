package com.kiluss.bookrate.network.api

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.widget.Toast
import com.kiluss.bookrate.R
import com.kiluss.bookrate.activity.LoginActivity
import com.kiluss.bookrate.utils.Const.Companion.AUTHENTICATION_IS_REQUIRED_CODE
import okhttp3.Interceptor
import okhttp3.Response
import java.io.IOException


class ResponseHeaderInterceptor(private val context: Context) : Interceptor {

    @Throws(IOException::class)
    override fun intercept(chain: Interceptor.Chain): Response {
        val response: Response = chain.proceed(chain.request())
        if (
            response.code() == AUTHENTICATION_IS_REQUIRED_CODE
        ) {
            loginAgain(context)
        }
        return response
    }

    private fun loginAgain(context: Context) {
        val pref: SharedPreferences =
            context.getSharedPreferences(
                context.getString(
                    R.string.saved_login_account_key
                ),
                Context.MODE_PRIVATE
            )
        val editor: SharedPreferences.Editor = pref.edit()
        editor.putBoolean(
            context.getString(R.string.is_sign_in_key),
            false
        ).apply()
        val intent = Intent(
            context,
            LoginActivity::class.java
        )
        intent.addFlags(Intent.FLAG_ACTIVITY_CLEAR_TOP)
        context.startActivity(intent)
    }
}
