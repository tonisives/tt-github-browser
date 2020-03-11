package com.tt.githubbrowser.network.client

import android.content.Context
import androidx.lifecycle.LiveData
import androidx.lifecycle.MutableLiveData
import com.tt.githubbrowser.R
import com.tt.githubbrowser.dao.SharedPreferencesDao
import com.tt.githubbrowser.model.Repo
import com.tt.githubbrowser.network.route.Repos

class RepoClient(ctx: Context, val prefs: SharedPreferencesDao) : ApiClient(ctx) {
    fun getRepos(): LiveData<ApiResponse<List<Repo>>> {
        val callback = MutableLiveData<ApiResponse<List<Repo>>>()
        val route = Repos(prefs.userCredentials)

        this.performRequest(route) { response ->
            if (response.statusCode == 200) {
                callback.value = ApiResponse(Repo.fromJson(response.json), null)
            } else {
                callback.value = ApiResponse(null, ctx.getString(R.string.default_error))
            }
        }

        return callback
    }
}