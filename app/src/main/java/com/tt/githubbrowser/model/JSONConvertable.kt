package com.tt.githubbrowser.model

import com.google.gson.Gson
import com.google.gson.GsonBuilder
import org.json.JSONObject
import java.util.*

interface JSONConvertable {
    fun toJSON(): String = Gson().toJson(this)
}

inline fun <reified T : JSONConvertable> String.toObject(): T {
    val builder = GsonBuilder()
    builder.registerTypeAdapter(Calendar::class.java, CalendarFromTimestampJsonDeserializer())
    val gson = builder.create()
    return gson.fromJson(this, T::class.java)
}

inline fun <reified T : JSONConvertable> JSONObject.toObject(): T {
    return this.toString().toObject()

}
