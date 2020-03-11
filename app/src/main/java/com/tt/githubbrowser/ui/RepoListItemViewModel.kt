package com.tt.githubbrowser.ui

import androidx.lifecycle.LiveData
import androidx.lifecycle.SavedStateHandle
import androidx.lifecycle.ViewModel
import com.tt.githubbrowser.model.Repo

class RepoListItemViewModel(savedStateHandle: SavedStateHandle) : ViewModel() {
    val carId : String = savedStateHandle["carId"] ?: throw IllegalArgumentException("missing user id")
    val repo : LiveData<Repo> = TODO()




// TODO: Implement the ViewModel
}
