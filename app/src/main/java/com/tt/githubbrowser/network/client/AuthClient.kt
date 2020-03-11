package com.tt.githubbrowser.network.client

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.tt.githubbrowser.R
import com.tt.githubbrowser.model.User
import com.tt.githubbrowser.model.toObject
import com.tt.githubbrowser.network.route.Login
import com.tt.githubbrowser.network.route.UserCredentials

class AuthClient(ctx: Context) : ApiClient(ctx) {
    fun loginUser(email: String, password: String): LiveData<ApiResponse<User>> {
        val callback = MutableLiveData<ApiResponse<User>>()

        val route = Login(UserCredentials(email, password))
        this.performRequest(route) { response ->
            when (response.statusCode) {
                200 -> {
                    val user = response.json.toObject<User>()
                    callback.value = ApiResponse(user, null)
                }
                401 -> {
                    callback.value = ApiResponse(null, ctx.getString(R.string.invalid_credentials))
                }
                else -> {
                    callback.value = ApiResponse(null, ctx.getString(R.string.login_unsuccessful))
                }
            }
        }

        return callback
    }
}