package com.tt.githubbrowser

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.tt.githubbrowser.dao.SharedPreferencesDao
import com.tt.githubbrowser.dao.UserDao
import com.tt.githubbrowser.model.User
import com.tt.githubbrowser.network.client.AuthClient
import com.tt.githubbrowser.network.route.UserCredentials
import com.tt.githubbrowser.repository.AuthRepository
import com.tt.githubbrowser.repository.Resource
import com.tt.githubbrowser.util.ApiUtil.successCall
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import util.CountingAppExecutors

class AuthRepoTest {
    private lateinit var repository: AuthRepository

    private val dao = mockk<UserDao>(relaxed = true)
    private val authClient = mockk<AuthClient>()
    private var sharedPrefs = mockk<SharedPreferencesDao>(relaxed = true)

    @get:Rule
    val instantExecutorRule = InstantTaskExecutorRule()

    @Before
    fun before() {
        repository = AuthRepository(
            authClient,
            dao,
            sharedPrefs,
            CountingAppExecutors().appExecutors
        )
    }

    @Test
    fun testLogin() {
        // test repository saves results into the database.
        val userName = "user"
        val token = "token"

        val user = createUser(userName, token)
        val userResponse = successCall(user)
        every { authClient.loginUser(userName, token) } returns userResponse
        every { dao.getFirst() } returns MutableLiveData<User>(user)

        val observer = mockk<Observer<Resource<User>>>(relaxed = true)
        repository.login(userName, token).observeForever(observer)

        verify { observer.onChanged(Resource.loading(null)) }

        // now user should be stored in user db and shared prefs should have token
        verify { dao.save(user) }
        verify { sharedPrefs.setCurrentUser(UserCredentials(userName, token)) }

        verify { observer.onChanged(Resource.success(user)) }
    }

    fun createUser(email: String, accessToken: String) = User(
        id = "1",
        login = email,
        token = accessToken
    )
}
