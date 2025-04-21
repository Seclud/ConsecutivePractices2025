package com.example.consecutivepractice

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.consecutivepractice.data.FavoritesRepository
import com.example.consecutivepractice.models.Developer
import com.example.consecutivepractice.models.Game
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class FavoritesViewModel(application: Application) : AndroidViewModel(application) {
    private val repository = FavoritesRepository(application)

    private val _uiState = MutableStateFlow(FavoritesUiState())
    val uiState: StateFlow<FavoritesUiState> = _uiState.asStateFlow()

    init {
        loadFavorites()
    }

    private fun loadFavorites() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true) }

            repository.allFavorites.collect { games ->
                _uiState.update {
                    it.copy(
                        favorites = games, isLoading = false
                    )
                }
            }
        }
    }

    suspend fun isGameFavorite(gameId: Int): Boolean {
        return repository.isFavorite(gameId)
    }

    suspend fun getFavoriteGameDescription(gameId: Int): String? {
        return repository.getFavoriteGameDescription(gameId)
    }

    suspend fun getFavoriteGameDevelopers(gameId: Int): List<Developer>? {
        return repository.getFavoriteGameDevelopers(gameId)
    }

    fun toggleFavorite(
        game: Game, description: String? = null, developers: List<Developer>? = null
    ) {
        viewModelScope.launch {
            repository.toggleFavorite(game, description, developers)
        }
    }
}

data class FavoritesUiState(
    val favorites: List<Game> = emptyList(), val isLoading: Boolean = false
)