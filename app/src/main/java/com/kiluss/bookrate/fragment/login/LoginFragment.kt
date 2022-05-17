package com.kiluss.bookrate.fragment.login

import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import com.kiluss.bookrate.activity.MainActivity
import com.kiluss.bookrate.data.model.LoginResponse
import com.kiluss.bookrate.databinding.FragmentLoginBinding
import com.kiluss.bookrate.network.api.BookService
import com.kiluss.bookrate.network.api.RetrofitClient
import org.json.JSONObject
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class LoginFragment : Fragment() {
    private lateinit var usernameEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var loadingProgressBar: ProgressBar
    private lateinit var loginApi: BookService
    private var _binding: FragmentLoginBinding? = null

    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater,
        container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {

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
        loginApi.login(username, password).enqueue(object : Callback<LoginResponse?> {
            override fun onResponse(
                call: Call<LoginResponse?>,
                response: Response<LoginResponse?>
            ) {
                if (response.isSuccessful) {
                    val loginResponse = response.body()
                    loadingProgressBar.visibility = View.GONE
                    requireActivity().startActivity(
                        Intent(
                            requireActivity(),
                            MainActivity::class.java
                        )
                    )
                    requireActivity().finish()
                    Log.e("onResponse: ", loginResponse.toString())
                } else {
                    loadingProgressBar.visibility = View.GONE
                    showToastError(response)
                }
            }

            override fun onFailure(call: Call<LoginResponse?>, t: Throwable) {
                loadingProgressBar.visibility = View.GONE
                Toast.makeText(requireContext(), t.message, Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun showToastError(response: Response<LoginResponse?>) {
        val responseJsonString = response.errorBody()?.charStream()?.readText().toString()
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

    private fun showLoginFailed(errorString: String) {
        val appContext = context?.applicationContext ?: return
        Toast.makeText(appContext, errorString, Toast.LENGTH_SHORT).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}