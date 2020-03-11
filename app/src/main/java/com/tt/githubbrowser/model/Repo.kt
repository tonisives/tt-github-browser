package com.tt.githubbrowser.model

import androidx.room.ColumnInfo
import androidx.room.Entity
import androidx.room.PrimaryKey
import com.google.gson.annotations.SerializedName
import org.json.JSONArray
import org.json.JSONObject
import java.util.*

@Entity
data class Repo(
    @SerializedName("id") @PrimaryKey val id: Int,
    @SerializedName("name") val name: String,
    @SerializedName("last_fetch") @ColumnInfo(name = "lastFetch") var lastFetch: Long = Calendar.getInstance().timeInMillis
) : JSONConvertable {
    companion object {
        fun fromJson(jsonObject: String): List<Repo> {
            val array = JSONArray(jsonObject)
            if ((array.length()) == 0) return emptyList()

            val repos = mutableListOf<Repo>()
            
            for (i in 0 until array.length()) {
                val json = array[i] as JSONObject
                val repo: Repo = json.toObject()
                repos.add(repo)
            }

            return repos
        }
    }
}