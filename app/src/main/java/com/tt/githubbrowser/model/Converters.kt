package com.tt.githubbrowser.model

import androidx.room.TypeConverter
import com.google.gson.JsonDeserializationContext
import com.google.gson.JsonDeserializer
import com.google.gson.JsonElement
import com.google.gson.JsonParseException
import java.lang.reflect.Type

import java.text.SimpleDateFormat
import java.util.*

class Converters {
    val dateFormat = SimpleDateFormat("yyyy-MM-dd'T'HH:mm:ssXXX", Locale.ENGLISH)

    @TypeConverter
    fun fromTimestamp(value: String?): Calendar? {
        val c = Calendar.getInstance()
        val date = dateFormat.parse(value?.replace("\"", ""))
        c.timeInMillis = date.time
        return c
    }

    @TypeConverter
    fun toTimestamp(timestamp: Calendar?): String? {
        if (timestamp == null) return null
        return dateFormat.format(timestamp?.timeInMillis)
    }
}

class CalendarFromTimestampJsonDeserializer : JsonDeserializer<Calendar?> {
    @Throws(JsonParseException::class)
    override fun deserialize(
        json: JsonElement, typeOfT: Type?,
        context: JsonDeserializationContext?
    ): Calendar {
        return Converters().fromTimestamp(json.toString())!!
    }
}

