package com.kiluss.bookrate.activity

import android.content.Context
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.util.Log
import android.widget.Toast
import com.google.gson.Gson
import com.kiluss.bookrate.R
import com.kiluss.bookrate.data.model.LoginResponse
import com.kiluss.bookrate.data.model.Account
import com.kiluss.bookrate.databinding.ActivityChangePasswordBinding
import com.kiluss.bookrate.network.api.BookService
import com.kiluss.bookrate.network.api.RetrofitClient
import okhttp3.RequestBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response

class ChangePasswordActivity : AppCompatActivity() {
    private lateinit var binding: ActivityChangePasswordBinding
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = ActivityChangePasswordBinding.inflate(layoutInflater)
        setContentView(binding.root)
        supportActionBar?.title = "Change password"

        binding.btnSubmit.setOnClickListener{
            if (binding.edtNewPassword.text.toString() == binding.edtConfirmNewPassword.text.toString()) {
                uploadChange()
            } else {
                Toast.makeText(this, "Confirm password not match", Toast.LENGTH_SHORT).show()
            }
        }
    }

    private fun uploadChange() {
        val sharedPref = getSharedPreferences(
            getString(R.string.saved_login_account_key),
            Context.MODE_PRIVATE
        )
        val gson = Gson()
        val json: String? = sharedPref.getString(getString(R.string.saved_login_account_key), "")
        val loginResponse = gson.fromJson(json, LoginResponse::class.java)

        RetrofitClient.getInstance(this)
            .getClientAuthorized(loginResponse.token.toString())
            .create(BookService::class.java)
            .changePassword(
                loginResponse.id.toString(),
                RequestBody.create(
                    okhttp3.MediaType.parse("application/json; charset=utf-8"),
                    createJsonObject().toString()
                )
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
                        response.code() == 400 -> {
                            Toast.makeText(
                                applicationContext,
                                "Current password not match",
                                Toast.LENGTH_SHORT
                            ).show()
                        }
                        response.isSuccessful -> {
                            Log.e("TAG", response.body().toString())
                            Toast.makeText(applicationContext, "Change password successfully!", Toast.LENGTH_SHORT).show()
                            finish()
                        }
                    }
                }

                override fun onFailure(call: Call<Account?>, t: Throwable) {
                    Log.e("TAG", t.stackTraceToString())
                    Toast.makeText(applicationContext, t.message, Toast.LENGTH_LONG).show()
                }
            })
    }

    private fun createJsonObject(): JSONObject {
        val json = JSONObject()
        json.put("currentPassword", binding.edtCurrentPassword.text)
        json.put("newPassword", binding.edtNewPassword.text)
        return json

    }
}