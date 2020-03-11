package com.tt.githubbrowser

import android.content.res.Resources
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.action.ViewActions.*
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents.intended
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.espresso.intent.rule.IntentsTestRule
import androidx.test.espresso.matcher.RootMatchers.withDecorView
import androidx.test.espresso.matcher.ViewMatchers.*
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.runner.AndroidJUnit4
import com.tt.githubbrowser.network.route.ApiRoute
import com.tt.githubbrowser.ui.LoginActivity
import com.tt.githubbrowser.ui.MainActivity
import com.tt.githubbrowser.util.ApiUtil.enqueueResponse
import okhttp3.mockwebserver.MockWebServer
import org.hamcrest.CoreMatchers.`is`
import org.hamcrest.CoreMatchers.not
import org.junit.BeforeClass
import org.junit.FixMethodOrder
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.MethodSorters

@RunWith(AndroidJUnit4::class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
class a_LoginTests {

    @get:Rule
    var intentsRule: IntentsTestRule<LoginActivity> = IntentsTestRule(LoginActivity::class.java)

    private val activity by lazy { intentsRule.activity!! }
    val res: Resources by lazy { activity.resources }
    var testRes = InstrumentationRegistry.getInstrumentation().targetContext.resources

    companion object {
        val appContext = InstrumentationRegistry.getInstrumentation().targetContext
        val mockWebServer: MockWebServer = MockWebServer()

        @BeforeClass
        @JvmStatic
        fun before() {
            appContext.deleteDatabase("app-db")
            ApiRoute.customUrl = mockWebServer.url("/test").toString()
        }
    }

    @Test
    fun a_invalidLoginReposNotShown() {
        mockWebServer.enqueueResponse("error_bad_credentials.json", 401)
        inputCredentials(testRes.getString(R.string.user), "2111")

        onView(withId(R.id.loginButton)).perform(click())

        // Wait for progress bar to finish
        val waitUntilNoProgressBar4 = WaitUntilViewVisible(activity, R.id.progressBar, false)
        IdlingRegistry.getInstance().register(waitUntilNoProgressBar4)

        // assert error shown
        onView(withText(R.string.invalid_credentials))
            .inRoot(withDecorView(not(`is`(activity.window.decorView))))
            .check(matches(isDisplayed()))
        IdlingRegistry.getInstance().unregister(waitUntilNoProgressBar4)
    }

    @Test
    fun b_afterLoginReposShown() {
        mockWebServer.enqueueResponse("login.json")
        inputCredentials(testRes.getString(R.string.user), testRes.getString(R.string.token))

        onView(withId(R.id.loginButton)).perform(click())

        // Wait for progress bar to finish
        val waitUntilNoProgressBar4 = WaitUntilViewVisible(activity, R.id.progressBar, false)
        IdlingRegistry.getInstance().register(waitUntilNoProgressBar4)

        // assert repos are shown
        intended(hasComponent(MainActivity::class.java.name))
        IdlingRegistry.getInstance().unregister(waitUntilNoProgressBar4)
    }

    private fun inputCredentials(user: String, password: String) {
        onView(withId(R.id.emailEditText)).perform(clearText())
        onView(withId(R.id.emailEditText)).perform(typeText(user))

        onView(withId(R.id.passwordEditText)).perform(clearText())
        onView(withId(R.id.passwordEditText)).perform(typeText(password))
        onView(withId(R.id.passwordEditText)).perform(closeSoftKeyboard())
    }
}
