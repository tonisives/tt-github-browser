package com.tt.githubbrowser

import androidx.lifecycle.MutableLiveData
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.launchActivity
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.espresso.matcher.RootMatchers
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.filters.LargeTest
import com.tt.githubbrowser.model.User
import com.tt.githubbrowser.repository.Resource
import com.tt.githubbrowser.ui.*
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.hamcrest.CoreMatchers
import org.hamcrest.CoreMatchers.not
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.koin.core.context.loadKoinModules
import org.koin.core.context.unloadKoinModules
import org.koin.core.module.Module
import org.koin.dsl.module

@LargeTest
class LoginActivityTests {
    private lateinit var scenario: ActivityScenario<LoginActivity>
    private lateinit var activity: LoginActivity

    private lateinit var loginViewModel: LoginViewModel
    private lateinit var loginRequest: MutableLiveData<Resource<User?>>

    private lateinit var module: Module

    @Before
    fun before() {
        loginViewModel = mockk(relaxed = true)

        loginRequest = MutableLiveData()
        every { loginViewModel.user } returns loginRequest

        module = module(createdAtStart = true, override = true) {
            single { loginViewModel }
            single { mockk<MainViewModel>(relaxed = true) }
            single { mockk<RepoListViewModel>(relaxed = true) }
        }

        loadKoinModules(module)
        scenario = launchActivity()
        scenario.onActivity {
            activity = it
        }
    }

    @After
    fun after() {
        scenario.close()
        unloadKoinModules(module)
    }

    @Test
    fun failedLoginShowsError() {
        inputCredentials("1", "1")

        onView(withId(R.id.loginButton)).perform(click())

        // mock loading
        loginRequest.postValue(Resource.loading(null))
        // assert loading
        onView(withId(R.id.progressBar)).check(matches(isDisplayed()))

        // mock response
        verify { loginViewModel.login(any(), any()) }

        loginRequest.postValue(Resource.error(activity.resources.getString(R.string.invalid_credentials), null))

        // assert error shown
        onView(withText(R.string.invalid_credentials))
            .inRoot(RootMatchers.withDecorView(not(CoreMatchers.`is`(activity.window.decorView))))
            .check(matches(isDisplayed()))
    }

    @Test
    fun successLoginShowsMainVm() {
        Intents.init()
        inputCredentials("1", "1")
        val user = User("1", "1", "1", 1)
        loginRequest.postValue(Resource.loading(null))
        onView(withId(R.id.progressBar)).check(matches(isDisplayed()))

        loginRequest.postValue(Resource.success(user))
        onView(withId(R.id.progressBar)).check(matches(not(isDisplayed())))
        intended(hasComponent(MainActivity::class.java.name))
        Intents.release()
    }

    private fun inputCredentials(user: String, password: String) {
        onView(withId(R.id.emailEditText)).perform(ViewActions.clearText())
        onView(withId(R.id.emailEditText)).perform(ViewActions.typeText(user))

        onView(withId(R.id.passwordEditText)).perform(ViewActions.clearText())
        onView(withId(R.id.passwordEditText)).perform(ViewActions.typeText(password), ViewActions.closeSoftKeyboard())
    }
}