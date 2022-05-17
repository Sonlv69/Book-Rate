package com.kiluss.bookrate.fragment.login

import android.R
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Button
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.fragment.app.Fragment
import androidx.navigation.NavController
import androidx.navigation.Navigation
import com.kiluss.bookrate.activity.LoginActivity
import com.kiluss.bookrate.activity.MainActivity
import com.kiluss.bookrate.data.model.RegisterResponse
import com.kiluss.bookrate.databinding.FragmentSignUpBinding
import com.kiluss.bookrate.network.api.BookService
import com.kiluss.bookrate.network.api.RetrofitClient
import retrofit2.Call
import retrofit2.Callback
import retrofit2.Response


class SignUpFragment : Fragment() {
    private var _binding: FragmentSignUpBinding? = null
    private val binding get() = _binding!!
    private lateinit var edtUsername: EditText
    private lateinit var edtPassword: EditText
    private lateinit var edtPasswordConfirm: EditText
    private lateinit var signupButton: Button
    private lateinit var loadingProgressBar: ProgressBar
    private lateinit var signupApi : BookService

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View {
        _binding = FragmentSignUpBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        signupApi = RetrofitClient.getClient().create(BookService::class.java)
        edtUsername = binding.edtUsername
        edtPassword = binding.edtPassword
        edtPasswordConfirm = binding.edtPasswordConfirm
        signupButton = binding.btnSignUp
        loadingProgressBar = binding.loading


        signupButton.setOnClickListener {
            loadingProgressBar.visibility = View.VISIBLE
            performSignUp()
        }
    }

    private fun performSignUp() {
        val username = edtUsername.text.toString()
        val password = edtPassword.text.toString()
        val passwordConfirm = edtPasswordConfirm.text.toString()
        signupApi.register(username, password, passwordConfirm).enqueue(object : Callback<RegisterResponse?> {
            override fun onResponse(
                call: Call<RegisterResponse?>,
                response: Response<RegisterResponse?>
            ) {
                if (response.isSuccessful) {
                    val registerResponse = response.body()
                    loadingProgressBar.visibility = View.GONE
                    edtUsername.text.clear()
                    edtPassword.text.clear()
                    edtPasswordConfirm.text.clear()
                    Toast.makeText(context, "Register successfully!", Toast.LENGTH_SHORT).show()
                    navigateToLoginFragment()
                    Log.e("onResponse: ", registerResponse.toString())
                } else {
                    loadingProgressBar.visibility = View.GONE
                    showSignUpFailed(response.body().toString())
                }
            }

            override fun onFailure(call: Call<RegisterResponse?>, t: Throwable) {
                loadingProgressBar.visibility = View.GONE
                Toast.makeText(requireContext(), t.message, Toast.LENGTH_LONG).show()
            }
        })
    }

    private fun navigateToLoginFragment() {
        (activity as LoginActivity).viewPager.currentItem = 0
    }

    private fun showSignUpFailed(errorString: String) {
        val appContext = context?.applicationContext ?: return
        Toast.makeText(appContext, errorString, Toast.LENGTH_LONG).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}
