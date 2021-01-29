package com.tt.githubbrowser.ui

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.tt.githubbrowser.repository.AuthRepository

class LoginViewModel(val handle: SavedStateHandle,
                     private val authRepository: AuthRepository) : ViewModel() {
    // login activity can observe if there is a logged in user.
    // if there is none, this user will be set after login call.
    val user = authRepository.getLoggedInUser()

    fun login(userName: String, password: String) {
        authRepository.login(userName, password)
    }
}
