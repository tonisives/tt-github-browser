package com.tt.githubbrowser

import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.SavedStateHandle
import androidx.test.core.app.ActivityScenario
import androidx.test.core.app.launchActivity
import androidx.test.espresso.Espresso.onView
import androidx.test.espresso.action.ViewActions.click
import androidx.test.espresso.assertion.ViewAssertions.matches
import androidx.test.espresso.intent.Intents
import androidx.test.espresso.intent.matcher.IntentMatchers.hasComponent
import androidx.test.espresso.matcher.ViewMatchers
import androidx.test.espresso.matcher.ViewMatchers.isDisplayed
import androidx.test.espresso.matcher.ViewMatchers.withId
import androidx.test.filters.LargeTest
import com.tt.githubbrowser.model.Repo
import com.tt.githubbrowser.model.User
import com.tt.githubbrowser.repository.AuthRepository
import com.tt.githubbrowser.repository.RepoRepository
import com.tt.githubbrowser.repository.Resource
import com.tt.githubbrowser.repository.UserRepository
import com.tt.githubbrowser.ui.*
import com.tt.githubbrowser.util.RecyclerViewMatcher
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.hamcrest.CoreMatchers.not
import org.junit.After
import org.junit.Before
import org.junit.Test
import org.koin.core.context.loadKoinModules
import org.koin.dsl.module
import java.lang.Thread.sleep

@LargeTest
class MainActivityTests {
    val userRepo: UserRepository = mockk()
    val authRepo: AuthRepository = mockk()
    val repoRepo: RepoRepository = mockk()
    val savedStateHandle: SavedStateHandle = mockk()

    lateinit var repolistViewModel: RepoListViewModel
    lateinit var mainViewModel: MainViewModel

    var repoData = MutableLiveData<Resource<List<Repo>>>()
    var userData = MutableLiveData<User>()
    lateinit var scenario: ActivityScenario<MainActivity>

    @Before
    fun before() {
        every { userRepo.getUser() } returns userData
        every { repoRepo.getRepos() } returns repoData


        // mock logged in user
        repolistViewModel = RepoListViewModel(savedStateHandle, userRepo, repoRepo)
        mainViewModel = MainViewModel(savedStateHandle, authRepo, userRepo)

        val module = module(true, true) {
            single { repolistViewModel }
            single { mainViewModel }
            single { mockk<LoginViewModel>(relaxed = true) }
        }

        loadKoinModules(module)
        scenario = launchActivity()
    }

    @After
    fun after() {
        scenario.close()
    }

    @Test
    fun listLoadingAndShown() {
        val repos = createRepos()
        repoData.postValue(Resource.loading(null))
        onView(withId(R.id.listProgressBar)).check(matches(isDisplayed()))

        sleep(10)
        repoData.postValue(Resource.success(repos))
        onView(withId(R.id.listProgressBar)).check(matches(not(isDisplayed())))
        onView(listMatcher().atPosition(0)).check(matches(ViewMatchers.hasDescendant(ViewMatchers.withText("Repo 1"))))
        onView(listMatcher().atPosition(1)).check(matches(ViewMatchers.hasDescendant(ViewMatchers.withText("Repo 2"))))
    }

    @Test
    fun logoutShowsLogin() {
        every { authRepo.logout() } returns mockk()

        Intents.init()
        onView(withId(R.id.logOutButton)).perform(click())
        verify { mainViewModel.logout() }

        // mock user deleted
        userData.postValue(null)

        Intents.intended(hasComponent(LoginActivity::class.java.name))
        Intents.release()
    }

    private fun createRepos() = listOf(
        Repo(id = 1, name = "Repo 1"), Repo(id = 2, name = "Repo 2")
    )

    private fun listMatcher(): RecyclerViewMatcher {
        return RecyclerViewMatcher(R.id.listView)
    }
}