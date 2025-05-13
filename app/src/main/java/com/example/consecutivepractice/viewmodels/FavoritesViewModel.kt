package com.example.consecutivepractice.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.consecutivepractice.data.FavoritesRepository
import com.example.consecutivepractice.models.Developer
import com.example.consecutivepractice.models.Game
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class FavoritesViewModel(private val repository: FavoritesRepository) : ViewModel() {

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

                games.forEach { game ->
                    loadFavoriteGameDetails(game.id)
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

    fun loadFavoriteGameDetails(gameId: Int) {
        if (_uiState.value.loadingGameDetails.contains(gameId) ||
            _uiState.value.gameDetailsCache.containsKey(gameId)
        ) {
            return
        }

        _uiState.update { state ->
            state.copy(loadingGameDetails = state.loadingGameDetails + gameId)
        }

        viewModelScope.launch {
            try {
                val description = repository.getFavoriteGameDescription(gameId)
                val developers = repository.getFavoriteGameDevelopers(gameId)

                _uiState.update { state ->
                    state.copy(
                        gameDetailsCache = state.gameDetailsCache + (gameId to FavoriteGameDetails(
                            description = description,
                            developers = developers,
                            isLoading = false
                        )),
                        loadingGameDetails = state.loadingGameDetails - gameId
                    )
                }
            } catch (e: Exception) {
                _uiState.update { state ->
                    state.copy(
                        gameDetailsCache = state.gameDetailsCache + (gameId to FavoriteGameDetails(
                            isLoading = false
                        )),
                        loadingGameDetails = state.loadingGameDetails - gameId
                    )
                }
            }
        }
    }

    fun toggleFavorite(
        game: Game, description: String? = null, developers: List<Developer>? = null
    ) {
        viewModelScope.launch {
            repository.toggleFavorite(game, description, developers)
        }
    }

    // Метод для принудительного обновления деталей для конкретной игры
    fun refreshGameDetails(gameId: Int) {
        _uiState.update { state ->
            state.copy(
                gameDetailsCache = state.gameDetailsCache - gameId,
                loadingGameDetails = state.loadingGameDetails - gameId
            )
        }
        loadFavoriteGameDetails(gameId)
    }
}

data class FavoritesUiState(
    val favorites: List<Game> = emptyList(),
    val isLoading: Boolean = false,
    val gameDetailsCache: Map<Int, FavoriteGameDetails> = emptyMap(),
    val loadingGameDetails: Set<Int> = emptySet()
)

data class FavoriteGameDetails(
    val description: String? = null,
    val developers: List<Developer>? = null,
    val isLoading: Boolean = false
)