package com.example.consecutivepractice

import android.app.Application
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.consecutivepractice.Network.RetrofitInstance
import com.example.consecutivepractice.models.Developer
import com.example.consecutivepractice.models.Game
import com.example.consecutivepractice.models.GameDetailsResponse
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.flow.update
import kotlinx.coroutines.launch

class GameViewModel(application: Application) : AndroidViewModel(application) {
    private val api = RetrofitInstance.getGamesApi(application.applicationContext)
    private val repository = GameRepository(api)
    private val filterRepository = FilterRepository(application)

    private val _uiState = MutableStateFlow(GameUiState())
    val uiState: StateFlow<GameUiState> = _uiState.asStateFlow()

    private val detailsCache = mutableMapOf<Int, GameDetailsResponse>()
    private val loadingDetails = mutableSetOf<Int>()

    init {
        fetchGames()

        viewModelScope.launch {
            filterRepository.hasCustomFilters.collect { hasCustomFilters ->
                _uiState.update { it.copy(filtersApplied = hasCustomFilters) }
                updateFilteredGames()
            }
        }
    }

    fun fetchGames() {
        viewModelScope.launch {
            _uiState.update { it.copy(isLoading = true, errorMessage = null) }

            val gamesList = repository.fetchGames()
            _uiState.update {
                it.copy(
                    allGames = gamesList,
                    isLoading = false
                )
            }

            updateFilteredGames()
        }
    }

    fun onSearchQueryChanged(query: String) {
        viewModelScope.launch {
            _uiState.update {
                it.copy(
                    searchQuery = query,
                    isLoading = true
                )
            }

            val results = repository.searchGames(query)
            _uiState.update {
                it.copy(
                    allGames = results,
                    isLoading = false
                )
            }

            updateFilteredGames()
        }
    }

    fun applyFilters(minRating: Float, genre: String, onlyRecent: Boolean) {
        filterRepository.saveFilters(minRating, genre, onlyRecent)
        updateFilteredGames()
    }

    private fun updateFilteredGames() {
        val currentState = _uiState.value
        val filteredList = if (currentState.filtersApplied) {
            filterRepository.filterGames(currentState.allGames)
        } else {
            currentState.allGames
        }

        _uiState.update { it.copy(filteredGames = filteredList) }
    }

    fun getGameDetails(gameId: Int, onComplete: (GameDetailsResponse?) -> Unit = {}) {
        if (detailsCache.containsKey(gameId)) {
            onComplete(detailsCache[gameId])
            return
        }

        if (loadingDetails.contains(gameId)) return

        loadingDetails.add(gameId)
        viewModelScope.launch {
            val details = repository.getGameDetailsOnce(gameId)
            if (details != null) {
                detailsCache[gameId] = details
            }
            onComplete(details)
            loadingDetails.remove(gameId)
        }
    }

    fun getGameDescription(gameId: Int, onResult: (String?) -> Unit) {
        getGameDetails(gameId) { details -> onResult(details?.description) }
    }

    fun getGameDevelopers(gameId: Int, onResult: (List<Developer>?) -> Unit) {
        getGameDetails(gameId) { details -> onResult(details?.developers) }
    }
}

data class GameUiState(
    val allGames: List<Game> = emptyList(),
    val filteredGames: List<Game> = emptyList(),
    val isLoading: Boolean = false,
    val errorMessage: String? = null,
    val searchQuery: String = "",
    val filtersApplied: Boolean = false
)
