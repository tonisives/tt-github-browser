package com.tt.githubbrowser

import android.content.res.Resources
import android.view.View
import androidx.recyclerview.widget.RecyclerView
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.IdlingRegistry
import androidx.test.espresso.NoMatchingViewException
import androidx.test.espresso.ViewAssertion
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers
import androidx.test.espresso.intent.rule.IntentsTestRule
import androidx.test.espresso.matcher.ViewMatchers.assertThat
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.platform.app.InstrumentationRegistry
import androidx.test.runner.AndroidJUnit4
import com.tt.githubbrowser.network.route.ApiRoute
import com.tt.githubbrowser.ui.LoginActivity
import com.tt.githubbrowser.ui.MainActivity
import com.tt.githubbrowser.util.ApiUtil.enqueueResponse
import com.tt.githubbrowser.util.waitUntilActivityVisible
import okhttp3.mockwebserver.MockWebServer
import org.hamcrest.CoreMatchers.`is`
import org.junit.BeforeClass
import org.junit.FixMethodOrder
import org.junit.Rule
import org.junit.Test
import org.junit.runner.RunWith
import org.junit.runners.MethodSorters

@RunWith(AndroidJUnit4::class)
@FixMethodOrder(MethodSorters.NAME_ASCENDING)
class b_RepoListTests {
    @get:Rule
    var intentsRule: IntentsTestRule<MainActivity> = IntentsTestRule(MainActivity::class.java)

    private val activity by lazy { intentsRule.activity!! }
    val res: Resources by lazy { activity.resources }
    var testRes = InstrumentationRegistry.getInstrumentation().targetContext.resources

    companion object {
        val mockWebServer: MockWebServer = MockWebServer()

        @BeforeClass
        @JvmStatic
        fun before() {
            ApiRoute.customUrl = mockWebServer.url("/test").toString()
        }
    }

    @Test
    fun a_repoListLoadingAndShown() {
        mockWebServer.enqueueResponse("repos.json")
        // wait for the spinner to finish

        val waitUntilNoProgressBar5 = WaitUntilViewVisible(activity, R.id.listProgressBar, false)
        IdlingRegistry.getInstance().register(waitUntilNoProgressBar5)

        // assert all items shown in list
        onView(withId(R.id.listView)).check(RecyclerViewItemCountAssertion(30))
        IdlingRegistry.getInstance().unregister(waitUntilNoProgressBar5)
    }

    class RecyclerViewItemCountAssertion(private val expectedCount: Int) : ViewAssertion {
        override fun check(view: View, noViewFoundException: NoMatchingViewException?) {
            if (noViewFoundException != null) {
                throw noViewFoundException
            }
            val recyclerView = view as RecyclerView
            val adapter = recyclerView.adapter
            assertThat(adapter!!.itemCount, `is`(expectedCount))
        }
    }

    @Test
    fun b_logout() {
        onView(withId(R.id.logOutButton)).perform(click())
        Intents.intended(IntentMatchers.hasComponent(LoginActivity::class.java.name))
    }
}
