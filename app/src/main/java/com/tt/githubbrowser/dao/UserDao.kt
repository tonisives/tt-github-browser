package com.tt.githubbrowser.dao

import androidx.lifecycle.LiveData
import androidx.room.*
import androidx.room.OnConflictStrategy.REPLACE
import com.tt.githubbrowser.model.User

@Dao
interface UserDao {
    @Insert(onConflict = REPLACE)
    fun save(user: User)

    @Update
    fun update(vararg users: User)

    @Delete
    fun delete(vararg users: User)

    @Query("DELETE FROM user")
    fun deleteAll()

    @Query("SELECT * FROM user WHERE id = :email")
    fun getByEmail(email: String): LiveData<User>

    @Query("SELECT * FROM user LIMIT 1")
    fun getFirst(): LiveData<User?>
}