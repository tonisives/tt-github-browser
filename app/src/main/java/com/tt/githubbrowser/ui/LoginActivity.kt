package com.tt.githubbrowser.ui

import android.content.Intent
import android.os.Bundle
import android.view.inputmethod.InputMethodManager
import android.widget.Toast
import android.widget.Toast.LENGTH_LONG
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.observe
import com.tt.githubbrowser.util.Credentials
import com.tt.githubbrowser.R
import com.tt.githubbrowser.databinding.LoginActivityBinding
import com.tt.githubbrowser.model.User
import com.tt.githubbrowser.repository.Status
import org.koin.androidx.viewmodel.ext.android.viewModel

class LoginActivity : AppCompatActivity() {
    private val viewModel: LoginViewModel by viewModel()
    private lateinit var binding: LoginActivityBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        binding = LoginActivityBinding.inflate(layoutInflater)
        setContentView(binding.root)

        binding.loginButton.setOnClickListener {
            if (binding.emailEditText.text.toString().isBlank() == false &&
                binding.passwordEditText.text.toString().isBlank() == false
            ) {
                login()
            }
        }

        val credentials = Credentials(this)
        credentials.getResource("user")?.let {
            binding.emailEditText.setText(it)
        }

        credentials.getResource("token")?.let {
            binding.passwordEditText.setText(it)
        }

        viewModel.user.observe(this) {
            it.status.showWhenLoading(binding.progressBar)
            it.status.hideWhenLoading(binding.loginView)

            when (it.status) {
                Status.SUCCESS -> {
                    val user = it.data
                    // currently this gets called 2 times on login. once from here and once when main activity calls
                    // get user. this means we have to have the activity as singleTop
                    if (user != null && user.token.isNullOrEmpty() == false) showMainActivity(user)
                }
                Status.ERROR -> {
                    Toast.makeText(this@LoginActivity, it.message, LENGTH_LONG).show()
                }
            }
        }
    }

    fun hideKeyboard() {
        val view = this.currentFocus
        if (view != null) {
            val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
            imm.hideSoftInputFromWindow(view.windowToken, 0)
        }
    }

    fun showKeyboard() {
        binding.emailEditText.requestFocus()
        val imm = getSystemService(INPUT_METHOD_SERVICE) as InputMethodManager
        imm.showSoftInput(binding.emailEditText, InputMethodManager.SHOW_IMPLICIT)
    }

    fun login() {
        hideKeyboard()
        viewModel.login(binding.emailEditText.text.toString(), binding.passwordEditText.text.toString())
    }

    private fun showMainActivity(user: User) {
        val intent = Intent(this, MainActivity::class.java)
        startActivity(intent)
        finish()
    }
}