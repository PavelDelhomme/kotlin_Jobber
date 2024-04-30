package com.delhomme.jobber.ex.converters
/*
import androidx.room.TypeConverter
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class TechnologyConverter {
    private val gson = Gson()

    @TypeConverter
    fun fromTechnologyList(technologies: List<String>): String {
        return gson.toJson(technologies)
    }

    @TypeConverter
    fun toTechnologyList(technologyString: String): List<String> {
        val listType = object : TypeToken<List<String>>() {}.type
        return gson.fromJson(technologyString, listType)
    }
}
*/