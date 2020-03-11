package com.tt.githubbrowser.dao

import android.content.Context
import android.content.SharedPreferences
import com.tt.githubbrowser.R
import com.tt.githubbrowser.network.route.UserCredentials

class SharedPreferencesDao(val context: Context?) {

    var userCredentials: UserCredentials? = null
        get() {
            if (field != null) return field

            val email = getCurrentUserEmail()
            if (email != null) {
                field = UserCredentials(
                    email,
                    getCurrentUserToken()!!
                )
            }

            return field
        }

    fun setCurrentUser(user: UserCredentials) {
        val sharedPref = getSharedPreferences() ?: return
        with(sharedPref.edit()) {
            putString(currentUserKey, user.email)
            putString(tokenKey, user.token)
            userCredentials = user
            commit()
        }
    }

    fun clearCurrentUser() {
        val sharedPref = getSharedPreferences() ?: return
        with(sharedPref.edit()) {
            remove(currentUserKey)
            remove(tokenKey)
            commit()
        }
    }

    private fun getSharedPreferences(): SharedPreferences? =
        context?.getSharedPreferences(
            context.getString(R.string.shared_preferences), Context.MODE_PRIVATE
        )


    val tokenKey = "token_key"
    val currentUserKey = "current_user_key"

    private fun getCurrentUserEmail(): String? {
        return context?.getSharedPreferences(
            context.getString(R.string.shared_preferences), Context.MODE_PRIVATE
        )?.getString(currentUserKey, null)
    }

    private fun getCurrentUserToken(): String? {
        return context?.getSharedPreferences(
            context.getString(R.string.shared_preferences), Context.MODE_PRIVATE
        )?.getString(tokenKey, null)
    }
}