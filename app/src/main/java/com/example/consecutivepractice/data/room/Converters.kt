package com.example.consecutivepractice.data.room

import androidx.room.TypeConverter
import com.example.consecutivepractice.models.Developer
import com.example.consecutivepractice.models.Genre
import com.example.consecutivepractice.models.PlatformWrapper
import com.google.gson.Gson
import com.google.gson.reflect.TypeToken

class Converters {
    private val gson = Gson()

    @TypeConverter
    fun fromGenreList(value: List<Genre>?): String? {
        return if (value == null) null else gson.toJson(value)
    }

    @TypeConverter
    fun toGenreList(value: String?): List<Genre>? {
        if (value == null) return null
        val type = object : TypeToken<List<Genre>>() {}.type
        return gson.fromJson(value, type)
    }

    @TypeConverter
    fun fromPlatformList(value: List<PlatformWrapper>?): String? {
        return if (value == null) null else gson.toJson(value)
    }

    @TypeConverter
    fun toPlatformList(value: String?): List<PlatformWrapper>? {
        if (value == null) return null
        val type = object : TypeToken<List<PlatformWrapper>>() {}.type
        return gson.fromJson(value, type)
    }

    @TypeConverter
    fun fromDeveloperList(value: List<Developer>?): String? {
        return if (value == null) null else gson.toJson(value)
    }

    @TypeConverter
    fun toDeveloperList(value: String?): List<Developer>? {
        if (value == null) return null
        val type = object : TypeToken<List<Developer>>() {}.type
        return gson.fromJson(value, type)
    }
}