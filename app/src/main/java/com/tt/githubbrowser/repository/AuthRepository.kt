package com.tt.githubbrowser.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.tt.githubbrowser.dao.SharedPreferencesDao

import com.tt.githubbrowser.dao.UserDao
import com.tt.githubbrowser.model.User

import com.tt.githubbrowser.network.client.AuthClient
import com.tt.githubbrowser.network.route.UserCredentials
import com.tt.githubbrowser.util.AppExecutors

class AuthRepository constructor(
    private val authClient: AuthClient,
    private val userDao: UserDao,
    private val sharedPrefs: SharedPreferencesDao,
    private val executor: AppExecutors
) {
    var result = MediatorLiveData<Resource<User>>()

    fun getLoggedInUser(): LiveData<Resource<User?>> {
        result.value = Resource.loading(null)

        executor.diskIO().execute {
            val dbSource = userDao.getFirst()
            executor.mainThread().execute {
                result.addSource(dbSource) { data ->
                    result.value = Resource.success(data)
                    result.removeSource(dbSource)
                }
            }
        }

        return result as LiveData<Resource<User?>>
    }

    fun login(email: String, token: String): LiveData<Resource<User>> {
        // call the login, store new user in db, set current user to sharedPrefs
        result.value = Resource.loading(null)

        val networkRequest = authClient.loginUser(email, token)

        result.addSource(networkRequest) { data ->
            result.removeSource(networkRequest)

            if (data.value != null) {
                data.value.token = token

                executor.diskIO().execute {
                    userDao.save(data.value) // now next requests can be made with this token
                    // the login request is not actually required to show the repo list, but we save it here because
                    // mostly API-s return access token after login.
                    sharedPrefs.setCurrentUser(UserCredentials(email, token))

                    executor.mainThread().execute {
                        result.addSource(userDao.getFirst()) {
                            result.value = Resource.success(it)
                        }
                    }
                }
            } else {
                result.value = Resource.error(data.errorMessage!!, null)
            }
        }

        return result
    }

    fun logout(): LiveData<Resource<Unit>> {
        val status = mutableLoadingLiveDataOf<Unit>()

        executor.diskIO().execute {
            userDao.deleteAll()
            status.postValue(Resource.success(null))
        }

        return status
    }
}