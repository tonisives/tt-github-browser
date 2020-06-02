package com.tt.githubbrowser

import androidx.arch.core.executor.testing.InstantTaskExecutorRule
import androidx.lifecycle.MutableLiveData
import androidx.lifecycle.Observer
import com.tt.githubbrowser.dao.RepoDao
import com.tt.githubbrowser.model.Repo
import com.tt.githubbrowser.network.client.RepoClient
import com.tt.githubbrowser.repository.RepoRepository
import com.tt.githubbrowser.repository.Resource
import com.tt.githubbrowser.util.ApiUtil
import util.InstantAppExecutors
import io.mockk.Called
import io.mockk.every
import io.mockk.mockk
import io.mockk.verify
import org.junit.Before
import org.junit.Rule
import org.junit.Test

class RepoRepoTest {
    private lateinit var repository: RepoRepository

    private lateinit var dao: RepoDao
    private lateinit var client: RepoClient

    @Rule
    @JvmField
    val instantExecutorRule = InstantTaskExecutorRule()

    @Before
    fun before() {
        dao = mockk(relaxed = true)
        client = mockk()
        repository = RepoRepository(client, dao, InstantAppExecutors())
    }

    @Test
    fun testReposFetchedAndStored() {
        // test repository saves results into the database.
        val dbData = MutableLiveData<List<Repo>>()
        every { dao.getRepos() } returns dbData

        // mock api response
        val repos = createRepos()
        val repoResponse = ApiUtil.successCall(repos)
        every { client.getRepos() } returns repoResponse

        // mock the observer
        val observer = mockk<Observer<Resource<List<Repo>>>>(relaxed = true)
        repository.getRepos().observeForever(observer)
        // we are getting repos from db
        verify { observer.onChanged(Resource.loading(null)) }
        verify { dao.getRepos() }
        // make sure no web calls at this point
        verify { client.getRepos() wasNot Called }

        // return null data from db
        dbData.postValue(null)
        // verify data fetched from remote and saved
        verify { client.getRepos() }
        verify { dao.save(repos) }

        // db calls that there is new data
        dbData.postValue(repos)
        // new db data post to repo listener
        verify { observer.onChanged(Resource.success(repos)) }
    }

    fun createRepos() = listOf(
        Repo(id = 1, name = "1"), Repo(id = 2, name = "2")
    )
}
