package com.kiluss.bookrate.fragment.login

import android.content.Intent
import android.os.Bundle
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.EditText
import android.widget.ProgressBar
import android.widget.Toast
import androidx.annotation.StringRes
import androidx.fragment.app.Fragment
import com.kiluss.bookrate.activity.MainActivity
import com.kiluss.bookrate.databinding.FragmentLoginBinding

class LoginFragment : Fragment() {
    private lateinit var usernameEditText: EditText
    private lateinit var passwordEditText: EditText
    private lateinit var loadingProgressBar: ProgressBar
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


        loginButton.setOnClickListener {
            loadingProgressBar.visibility = View.VISIBLE
            performCheckLogin();

        }
    }

    private fun performCheckLogin() {
        if (usernameEditText.text.toString() == "" && passwordEditText.text.toString() == "") {
            loadingProgressBar.visibility = View.GONE
            requireActivity().startActivity(Intent(requireActivity(), MainActivity::class.java))
            requireActivity().finish()
        } else {
            loadingProgressBar.visibility = View.GONE
            showLoginFailed("Username or Password is not correct")
        }
    }

    private fun showLoginFailed(errorString: String) {
        val appContext = context?.applicationContext ?: return
        Toast.makeText(appContext, errorString, Toast.LENGTH_LONG).show()
    }

override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}