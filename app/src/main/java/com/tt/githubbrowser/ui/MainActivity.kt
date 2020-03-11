package com.tt.githubbrowser.ui

import android.content.Intent
import android.os.Bundle
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.LifecycleOwner
import androidx.lifecycle.observe
import com.tt.githubbrowser.R
import kotlinx.android.synthetic.main.activity_main.*
import org.koin.androidx.viewmodel.ext.android.stateViewModel

class MainActivity : AppCompatActivity(), LifecycleOwner {
    private val viewModel: MainViewModel by stateViewModel()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        viewModel.user.observe(this) {
            // update UI

            if (it != null) {
                emailTextView.text = it.login
            } else {
                // user was successfully deleted. start login
                // currently this gets called 2 times. once from here and once from loginViewModel which calls
                // get user. this means we have to have the activity as singleTop
                val intent = Intent(this, LoginActivity::class.java)
                startActivity(intent)
                finish()
            }
        }

        // TODO: 07.02.2020 if some request 401, kill this activity and show login again

        logOutButton.setOnClickListener {
            viewModel.logout()
        }
    }
}
