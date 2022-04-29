package com.kiluss.bookrate.fragment.login

import android.os.Bundle
import androidx.fragment.app.Fragment
import android.view.LayoutInflater
import android.view.View
import android.view.ViewGroup
import android.widget.Toast
import androidx.annotation.StringRes
import com.kiluss.bookrate.R
import com.kiluss.bookrate.databinding.FragmentLoginBinding
import com.kiluss.bookrate.databinding.FragmentSignUpBinding


class SignUpFragment : Fragment() {
    private var _binding: FragmentSignUpBinding? = null
    // This property is only valid between onCreateView and
    // onDestroyView.
    private val binding get() = _binding!!

    override fun onCreateView(
        inflater: LayoutInflater, container: ViewGroup?,
        savedInstanceState: Bundle?
    ): View? {
        _binding = FragmentSignUpBinding.inflate(inflater, container, false)
        return binding.root
    }

    override fun onViewCreated(view: View, savedInstanceState: Bundle?) {
        super.onViewCreated(view, savedInstanceState)
        val edtUsername = binding.edtUsername
        val edtUsernameConfirm = binding.edtUsernameConfirm
        val edtPassword = binding.edtPassword
        val edtPasswordConfirm = binding.edtPasswordConfirm
        val loginButton = binding.btnSignUp
        val loadingProgressBar = binding.loading


        loginButton.setOnClickListener {
            loadingProgressBar.visibility = View.VISIBLE

        }
    }

    private fun showLoginFailed(@StringRes errorString: Int) {
        val appContext = context?.applicationContext ?: return
        Toast.makeText(appContext, errorString, Toast.LENGTH_LONG).show()
    }

    override fun onDestroyView() {
        super.onDestroyView()
        _binding = null
    }
}