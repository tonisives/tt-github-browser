package com.tt.githubbrowser.repository

import androidx.lifecycle.LiveData
import androidx.lifecycle.MediatorLiveData
import com.tt.githubbrowser.dao.RepoDao
import com.tt.githubbrowser.model.Repo
import com.tt.githubbrowser.network.client.RepoClient
import com.tt.githubbrowser.util.AppExecutors
import com.tt.githubbrowser.util.RateLimiter
import org.koin.core.component.KoinComponent
import java.util.concurrent.TimeUnit

class RepoRepository(
    private val repoClient: RepoClient,
    private val repoDao: RepoDao,
    private val executor: AppExecutors
) : KoinComponent {
    private val rateLimit = RateLimiter<String>(10, TimeUnit.SECONDS)
    private var result = MediatorLiveData<Resource<List<Repo>>>()

    fun getRepos(): LiveData<Resource<List<Repo>>> {
        // load user from the db, then get repos with access token in that user
        result.value = Resource.loading(null)

        val repoResource = object : NetworkBoundResource<List<Repo>, List<Repo>>(executor) {
            override fun saveCallResult(item: List<Repo>) = repoDao.save(item)

            override fun shouldFetch(data: List<Repo>?): Boolean {
                return data == null || data.isEmpty() || rateLimit.shouldFetch("repos")
            }

            override fun loadFromDb() = repoDao.getRepos()

            override fun createCall() = repoClient.getRepos()

            override fun onFetchFailed() = rateLimit.reset("repos")

        }.asLiveData()

        result.addSource(repoResource) { repoResult ->
            result.value = repoResult
        }

        return result
    }
}