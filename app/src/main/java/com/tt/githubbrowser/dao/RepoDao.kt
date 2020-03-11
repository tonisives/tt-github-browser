package com.tt.githubbrowser.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import androidx.room.OnConflictStrategy.REPLACE
import com.tt.githubbrowser.model.Repo
import java.util.*

@Dao
interface RepoDao {
    @Insert(onConflict = REPLACE)
    fun save(user: Repo)

    @Insert(onConflict = REPLACE)
    fun save(users: List<Repo>)

    @Update
    fun update(vararg users: Repo)

    @Delete
    fun delete(vararg users: Repo)

    // get a single repo
    @Query("SELECT * FROM repo WHERE id = :id")
    fun get(id: String): LiveData<Repo>

    @Query("SELECT * FROM repo")
    fun getRepos(): LiveData<List<Repo>>

    @Query("SELECT * FROM repo WHERE lastFetch >= (:now - :timeout)")
    fun getRepos(
        timeout: Long,
        now: Long = Calendar.getInstance().timeInMillis
    ): LiveData<List<Repo>>
}