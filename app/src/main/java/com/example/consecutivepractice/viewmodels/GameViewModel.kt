package com.example.consecutivepractice.viewmodels

import androidx.lifecycle.ViewModel
import androidx.lifecycle.viewModelScope
import com.example.consecutivepractice.domain.GameFilterUseCase
import com.example.consecutivepractice.models.Game
import com.example.consecutivepractice.models.GameDetailsResponse
import com.example.consecutivepractice.repositories.FilterRepository
import com.example.consecutivepractice.repositories.GameRepository
import kotlinx.coroutines.CoroutineExceptionHandler
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class GameViewModel(
    private val repository: GameRepository,
    private val filterRepository: FilterRepository,
    private val gameFilterUseCase: GameFilterUseCase
) : ViewModel() {
    private val _uiState = MutableStateFlow(GameUiState())
    val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()

    private val exceptionHandler = CoroutineExceptionHandler { _, throwable ->
        _uiState.update {
            it.copy(
                isLoading = false,
                errorMessage = throwable.message ?: "Неизвестная ошибка"
            )
        }
    }

    init {
        fetchGames()
        viewModelScope.launch(exceptionHandler) {
            filterRepository.hasCustomFilters.collect { hasCustomFilters ->
                _uiState.update { it.copy(filtersApplied = hasCustomFilters) }
                updateFilteredGames()
            }
        }
    }

    fun fetchGames() {
        viewModelScope.launch(exceptionHandler) {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }
            val gamesList = repository.fetchGames()
            _uiState.update {
                it.copy(
                    allGames = gamesList,
                    isLoading = false,
                    errorMessage = null
                )
            }
            updateFilteredGames()

            prefetchGameDetails(gamesList)
        }
    }

    private fun prefetchGameDetails(games: List<Game>) {
        viewModelScope.launch(exceptionHandler) {
            games.forEach { game ->
                if (!_uiState.value.loadingDetails.contains(game.id) &&
                    !_uiState.value.detailsCache.containsKey(game.id)
                ) {
                    getGameDetails(game.id)
                }
            }
        }
    }

    fun onSearchQueryChanged(query: String) {
        viewModelScope.launch(exceptionHandler) {
            _uiState.update {
                it.copy(
                    searchQuery = query,
                    isLoading = true,
                    errorMessage = null
                )
            }
            val results = repository.searchGames(query)
            _uiState.update {
                it.copy(
                    allGames = results,
                    isLoading = false,
                    errorMessage = null
                )
            }
            updateFilteredGames()

            prefetchGameDetails(results)
        }
    }

    fun applyFilters(minRating: Float, genre: String, onlyRecent: Boolean) {
        filterRepository.saveFilters(minRating, genre, onlyRecent)
        updateFilteredGames()

        val filteredGames = _uiState.value.filteredGames
        prefetchGameDetails(filteredGames)
    }

    private fun updateFilteredGames() {
        val currentState = _uiState.value
        val filteredList = if (currentState.filtersApplied) {
            gameFilterUseCase.filterGames(currentState.allGames)
        } else {
            currentState.allGames
        }
        _uiState.update { it.copy(filteredGames = filteredList) }
    }

    fun getGameDetails(gameId: Int, onComplete: (GameDetailsResponse?) -> Unit = {}) {
        val currentCache = _uiState.value.detailsCache
        val currentLoading = _uiState.value.loadingDetails

        if (currentCache.containsKey(gameId)) {
            onComplete(currentCache[gameId])
            return
        }

        if (currentLoading.contains(gameId)) return

        _uiState.update { it.copy(loadingDetails = it.loadingDetails + gameId) }

        viewModelScope.launch(exceptionHandler) {
            val details = repository.getGameDetailsOnce(gameId)

            _uiState.update {
                it.copy(
                    detailsCache = it.detailsCache + (gameId to details),
                    loadingDetails = it.loadingDetails - gameId
                )
            }

            onComplete(details)
        }
    }

    fun loadCurrentGameDetails(gameId: Int, favoritesViewModel: FavoritesViewModel) {
        if (gameId == _uiState.value.currentGameId &&
            _uiState.value.currentGameDetails != null
        ) {
            return
        }

        _uiState.update {
            it.copy(
                currentGameId = gameId,
                isLoadingCurrentGame = true,
                currentGameError = null,
                currentGameDetails = null
            )
        }

        viewModelScope.launch(exceptionHandler) {
            val details = repository.getGameDetailsOnce(gameId)

            val isFavorite = favoritesViewModel.isGameFavorite(gameId)

            _uiState.update {
                it.copy(
                    currentGameDetails = details,
                    isLoadingCurrentGame = false,
                    currentGameError = if (details == null) "Failed to load game details" else null,
                    isCurrentGameFavorite = isFavorite,
                    detailsCache = it.detailsCache + (gameId to details)
                )
            }
        }
    }

    fun setGameId(gameId: Int, favoritesViewModel: FavoritesViewModel) {
        if (gameId != _uiState.value.currentGameId) {
            loadCurrentGameDetails(gameId, favoritesViewModel)
        }
    }

    fun updateCurrentGameFavoriteStatus(isFavorite: Boolean) {
        _uiState.update {
            it.copy(isCurrentGameFavorite = isFavorite)
        }
    }
}

data class GameUiState(
    val allGames: List<Game> = emptyList(),
    val filteredGames: List<Game> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val searchQuery: String = "",
    val filtersApplied: Boolean = false,
    val detailsCache: Map<Int, GameDetailsResponse?> = emptyMap(),
    val loadingDetails: Set<Int> = emptySet(),
    val currentGameDetails: GameDetailsResponse? = null,
    val currentGameId: Int? = null,
    val isLoadingCurrentGame: Boolean = false,
    val currentGameError: String? = null,
    val isCurrentGameFavorite: Boolean = false
)
