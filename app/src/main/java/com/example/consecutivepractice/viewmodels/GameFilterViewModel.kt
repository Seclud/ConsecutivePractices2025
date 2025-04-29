package com.example.consecutivepractice.viewmodels

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.consecutivepractice.repositories.FilterRepository
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

class GameFilterViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = FilterRepository(application)

    private val _uiState = MutableStateFlow(FilterUiState())
    val uiState: StateFlow<FilterUiState> = _uiState.asStateFlow()

    init {
        loadFilters()
    }

    private fun loadFilters() {
        viewModelScope.launch {
            val filters = repository.loadFilters()
            _uiState.value = FilterUiState(
                minRating = filters.minRating,
                selectedGenre = filters.genre,
                onlyRecentGames = filters.onlyRecent
            )
        }
    }

    fun updateMinRating(rating: Float) {
        _uiState.value = _uiState.value.copy(minRating = rating)
        saveFilters()
    }

    fun updateSelectedGenre(genre: String) {
        _uiState.value = _uiState.value.copy(selectedGenre = genre)
        saveFilters()
    }

    fun updateOnlyRecentGames(onlyRecent: Boolean) {
        _uiState.value = _uiState.value.copy(onlyRecentGames = onlyRecent)
        saveFilters()
    }

    private fun saveFilters() {
        val currentState = _uiState.value
        repository.saveFilters(
            minRating = currentState.minRating,
            genre = currentState.selectedGenre,
            onlyRecent = currentState.onlyRecentGames
        )
    }

    fun resetFilters() {
        _uiState.value = FilterUiState()
        repository.resetFilters()
    }
}


data class FilterUiState(
    val minRating: Float = FilterRepository.DEFAULT_MIN_RATING,
    val selectedGenre: String = FilterRepository.DEFAULT_GENRE,
    val onlyRecentGames: Boolean = FilterRepository.DEFAULT_ONLY_RECENT
)