package com.example.consecutivepractice.repositories

import android.content.Context
import com.example.consecutivepractice.di.AndroidContextProvider
import com.example.consecutivepractice.di.FilterBadgeCache
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow

class FilterRepository(contextProvider: AndroidContextProvider) {
    private val sharedPreferences =
        contextProvider.getSharedPreferences(FILTER_PREFERENCES, Context.MODE_PRIVATE)

    private val _hasCustomFilters = MutableStateFlow(false)
    val hasCustomFilters: StateFlow<Boolean> = _hasCustomFilters.asStateFlow()


    init {
        val hasCustomSettings =
            sharedPreferences.getFloat(KEY_MIN_RATING, DEFAULT_MIN_RATING) > DEFAULT_MIN_RATING ||
                    sharedPreferences.getString(KEY_GENRE, DEFAULT_GENRE) != DEFAULT_GENRE ||
                    sharedPreferences.getBoolean(
                        KEY_ONLY_RECENT,
                        DEFAULT_ONLY_RECENT
                    ) != DEFAULT_ONLY_RECENT

        _hasCustomFilters.value = hasCustomSettings
        FilterBadgeCache.updateBadgeVisibility(hasCustomSettings)
    }

    data class FilterSettings(
        val minRating: Float = DEFAULT_MIN_RATING,
        val genre: String = DEFAULT_GENRE,
        val onlyRecent: Boolean = DEFAULT_ONLY_RECENT
    )

    fun loadFilters(): FilterSettings {
        return FilterSettings(
            minRating = sharedPreferences.getFloat(KEY_MIN_RATING, DEFAULT_MIN_RATING),
            genre = sharedPreferences.getString(KEY_GENRE, DEFAULT_GENRE) ?: DEFAULT_GENRE,
            onlyRecent = sharedPreferences.getBoolean(KEY_ONLY_RECENT, DEFAULT_ONLY_RECENT)
        )
    }

    fun saveFilters(minRating: Float, genre: String, onlyRecent: Boolean) {
        sharedPreferences.edit().apply {
            putFloat(KEY_MIN_RATING, minRating)
            putString(KEY_GENRE, genre)
            putBoolean(KEY_ONLY_RECENT, onlyRecent)
            apply()
        }

        val hasCustomSettings = minRating > DEFAULT_MIN_RATING ||
                genre != DEFAULT_GENRE ||
                onlyRecent != DEFAULT_ONLY_RECENT

        _hasCustomFilters.value = hasCustomSettings
        FilterBadgeCache.updateBadgeVisibility(hasCustomSettings)
    }

    fun resetFilters() {
        saveFilters(DEFAULT_MIN_RATING, DEFAULT_GENRE, DEFAULT_ONLY_RECENT)
    }

    companion object {
        private const val FILTER_PREFERENCES = "game_filter_preferences"
        private const val KEY_MIN_RATING = "min_rating"
        private const val KEY_GENRE = "genre"
        private const val KEY_ONLY_RECENT = "only_recent"

        const val DEFAULT_MIN_RATING = 0f
        const val DEFAULT_GENRE = "All"
        const val DEFAULT_ONLY_RECENT = false
    }
}