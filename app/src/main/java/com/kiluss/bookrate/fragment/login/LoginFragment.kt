package com.kiluss.bookrate.fragment.login

import android.content.Context
import android.content.Intent
import android.content.SharedPreferences
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.kiluss.bookrate.R
import com.kiluss.bookrate.activity.MainActivity
import com.kiluss.bookrate.data.model.LoginResponse
import com.kiluss.bookrate.databinding.FragmentLoginBinding
import com.kiluss.bookrate.network.api.BookService
import com.kiluss.bookrate.network.api.RetrofitClient
import okhttp3.RequestBody
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class LoginFragment : Fragment() {
    private lateinit var usernameEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var loadingProgressBar: ProgressBar
    private lateinit var loginApi: BookService
    private var isLogin = false
    private var _binding: FragmentLoginBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onAttach(context: Context) {
        super.onAttach(context)
        val sharedPref = requireActivity().getSharedPreferences(
            requireActivity().getString(R.string.saved_access_token_key),
            Context.MODE_PRIVATE
        )
        isLogin =
            sharedPref.getBoolean(requireActivity().getString(R.string.is_sign_in_key), false)
        if (isLogin) {
            requireActivity().startActivity(
                Intent(
                    requireActivity(),
                    MainActivity::class.java
                )
            )
            requireActivity().finish()
        }
    }

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {

        _binding = FragmentLoginBinding.inflate(inflater, container, false)
        return binding.root

    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        usernameEditText = binding.username
        passwordEditText = binding.password
        val loginButton = binding.btnSignIn
        loadingProgressBar = binding.loading
        loginApi = RetrofitClient.getClient().create(BookService::class.java)


        loginButton.setOnClickListener {
            loadingProgressBar.visibility = View.VISIBLE
            performCheckLogin()
        }
    }

    private fun performCheckLogin() {
        val username = usernameEditText.text.toString()
        val password = passwordEditText.text.toString()
        loginApi.login(createJsonRequestBody(
            "username" to username, "password" to password)).enqueue(object : Callback<String?> {
            override fun onResponse(
                call: Call<String?>,
                response: Response<String?>
            ) {
                when {
                    response.code() == 404 -> {
                        Toast.makeText(requireContext(), "Url is not exist", Toast.LENGTH_SHORT).show()
                    }
                    response.isSuccessful -> {
                        val loginResponse = response.body()
                        loginResponse?.let {
                            saveLoginInfo(loginResponse)
                        }
                        loadingProgressBar.visibility = View.GONE
                        requireActivity().startActivity(
                            Intent(
                                requireActivity(),
                                MainActivity::class.java
                            )
                        )
                        requireActivity().finish()
                    }
                    else -> {
                        showToastError(response)
                    }
                }
                loadingProgressBar.visibility = View.GONE
            }

            override fun onFailure(call: Call<String?>, t: Throwable) {
                loadingProgressBar.visibility = View.GONE
                t.printStackTrace()
                Toast.makeText(requireContext(), t.message, Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun saveLoginInfo(token: String) {
        val pref: SharedPreferences =
            requireContext().getSharedPreferences(
                requireContext().getString(
                    R.string.saved_access_token_key
                ),
                Context.MODE_PRIVATE
            )
        val editor: SharedPreferences.Editor = pref.edit()
        editor.putString(
            requireContext().getString(R.string.saved_access_token_key),
            token
        ).apply()
        editor.putBoolean(
            requireContext().getString(R.string.is_sign_in_key),
            true
        ).apply()
    }

    private fun createJsonRequestBody(vararg params : Pair<String, Any>) =
        RequestBody.create(
            okhttp3.MediaType.parse("application/json; charset=utf-8"),
            JSONObject(mapOf(*params)).toString())

    private fun showToastError(response: Response<String?>) {
        val responseJsonString = response.errorBody()?.charStream()?.readText().toString()
        if (responseJsonString.isNotBlank()) {
            if (responseJsonString.contains("errors")) {
                val errorString = JSONObject(responseJsonString).getString("errors")
                var passwordErrorString = ""
                var usernameErrorString = ""
                if (errorString.contains("Password")) {
                    passwordErrorString = JSONObject(errorString).getString("Password")
                    passwordErrorString = passwordErrorString.replace("[\"", "").replace("\"]", "")
                }
                if (errorString.contains("UserName")) {
                    usernameErrorString = JSONObject(errorString).getString("UserName")
                    usernameErrorString = usernameErrorString.replace("[\"", "").replace("\"]", "")
                }
                showLoginFailed("$passwordErrorString $usernameErrorString")
            } else {
                showLoginFailed(JSONObject(responseJsonString).getString("message"))
            }
        }
    }

    private fun showLoginFailed(errorString: String) {
        val appContext = context?.applicationContext ?: return
        Toast.makeText(appContext, errorString, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}