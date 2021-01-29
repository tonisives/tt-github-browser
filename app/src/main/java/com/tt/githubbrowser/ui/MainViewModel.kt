package com.tt.githubbrowser.ui;

import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.tt.githubbrowser.repository.AuthRepository
import com.tt.githubbrowser.repository.UserRepository

class MainViewModel(
    val savedStateHandle: SavedStateHandle,
    private val authRepository: AuthRepository,
    private val userRepository: UserRepository
) : ViewModel() {
    val user = userRepository.getUser()
    fun logout() = authRepository.logout()
}
