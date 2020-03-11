package com.tt.githubbrowser

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.tt.githubbrowser.dao.RepoDao
import com.tt.githubbrowser.dao.SharedPreferencesDao
import com.tt.githubbrowser.model.Repo
import com.tt.githubbrowser.network.client.RepoClient
import com.tt.githubbrowser.repository.RepoRepository
import com.tt.githubbrowser.repository.Resource
import com.tt.githubbrowser.util.ApiUtil
import com.tt.githubbrowser.util.InstantAppExecutors
import com.tt.githubbrowser.util.mock
import org.junit.Before
import org.junit.Rule
import org.junit.Test
import org.mockito.Mockito.*

class b_RepoRepoTest {
    private lateinit var repository: RepoRepository
    private val dao = mock(RepoDao::class.java)
    private val client = mock(RepoClient::class.java)
    private lateinit var sharedPrefs: SharedPreferencesDao

    @Rule
    @JvmField
    val instantExecutorRule = InstantTaskExecutorRule()

    @Before
    fun init() {
        sharedPrefs = mock(SharedPreferencesDao::class.java)
        repository = RepoRepository(client, dao, InstantAppExecutors())
    }

    @Test
    fun testReposFetchedAndStored() {
        // test repository saves results into the database.
        val dbData = MutableLiveData<List<Repo>>()
        `when`(dao.getRepos()).thenReturn(dbData)

        // mock api response
        val repos = createRepos()
        val repoResponse = ApiUtil.successCall(repos)
        `when`(client.getRepos()).thenReturn(repoResponse)

        // mock the observer
        val observer = mock<Observer<Resource<List<Repo>>>>()
        repository.getRepos().observeForever(observer)
        // we are getting repos from db
        verify(observer).onChanged(Resource.loading(null))
        verify(dao).getRepos()
        // make sure no web calls at this point
        verifyNoMoreInteractions(client)

        // return null data from db
        dbData.postValue(null)
        // verify data fetched from remote and saved
        verify(client).getRepos()
        verify(dao).save(repos)

        // db calls that there is new data
        dbData.postValue(repos)
        // new db data post to repo listener
        verify(observer).onChanged(Resource.success(repos))
    }

    fun createRepos() = listOf(
        Repo(id = 1, name = "1"), Repo(id = 2, name = "2")
    )
}
