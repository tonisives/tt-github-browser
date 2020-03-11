package com.tt.githubbrowser

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.Observer
import com.tt.githubbrowser.dao.AppDatabase
import com.tt.githubbrowser.dao.SharedPreferencesDao
import com.tt.githubbrowser.dao.UserDao
import com.tt.githubbrowser.model.User
import com.tt.githubbrowser.network.client.AuthClient
import com.tt.githubbrowser.network.route.UserCredentials
import com.tt.githubbrowser.repository.AuthRepository
import com.tt.githubbrowser.repository.Resource
import com.tt.githubbrowser.util.ApiUtil.successCall
import com.tt.githubbrowser.util.InstantAppExecutors
import com.tt.githubbrowser.util.mock
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.*

class a_AuthRepoTest {
    private val dao = mock(UserDao::class.java)
    private lateinit var authClient: AuthClient
    private lateinit var repository: AuthRepository
    private var sharedPrefs = mock(SharedPreferencesDao::class.java)

    @Rule
    @JvmField
    val instantExecutorRule = InstantTaskExecutorRule()

    @Before
    fun init() {
        val db = mock(AppDatabase::class.java)
        authClient = mock(AuthClient::class.java)
        sharedPrefs = mock(SharedPreferencesDao::class.java)

        `when`(db.userDao()).thenReturn(dao)

        repository = AuthRepository(
            authClient,
            dao,
            sharedPrefs,
            InstantAppExecutors()
        )
    }

    @Test
    fun testLogin() {
        // test repository saves results into the database.
        val userName = "user"
        val token = "token"

        val user = createUser(userName, token)
        val userResponse = successCall(user)
        `when`(authClient.loginUser(userName, token)).thenReturn(userResponse)

        val observer = mock<Observer<Resource<User>>>()
        repository.login(userName, token).observeForever(observer)

        // now user should be stored in user db and shared prefs should have token
        verify(dao).save(user)
        verify(sharedPrefs).setCurrentUser(UserCredentials(userName, token))
    }

    fun createUser(email: String, accessToken: String) = User(
        id = "1",
        login = email,
        token = accessToken
    )
}
