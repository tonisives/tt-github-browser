package com.tt.githubbrowser.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import java.util.*

@Entity
data class User(
    @SerializedName("id") @PrimaryKey var id: String,
    @SerializedName("login") val login: String,
    @SerializedName("token") var token: String,
    @SerializedName("last_fetch") @ColumnInfo(name = "lastFetch") var lastFetch: Long = Calendar.getInstance().timeInMillis
//    @Relation(parentColumn = "email", entityColumn = "userEmail")
//    val repos: List<Repo>
) : JSONConvertable {

}