package com.example.consecutivepractice

import android.app.Application
import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateMapOf
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.example.consecutivepractice.Network.RetrofitInstance
import com.example.consecutivepractice.models.Developer
import com.example.consecutivepractice.models.Game
import com.example.consecutivepractice.models.GameDetailsResponse
import kotlinx.coroutines.launch

class GameViewModel(application: Application) : AndroidViewModel(application) {
    private val api = RetrofitInstance.getGamesApi(application.applicationContext)
    private val repository = GameRepository(api)

    val gamesList: State<List<Game>> = repository.games
    val loading: State<Boolean> = repository.loading
    val error: State<String?> = repository.error

    val gameDetails: State<GameDetailsResponse?> = repository.gameDetails
    val detailsLoading: State<Boolean> = repository.detailsLoading
    val detailsError: State<String?> = repository.detailsError

    private val _gameDescriptions = mutableStateMapOf<Int, String?>()
    val gameDescriptions: Map<Int, String?> = _gameDescriptions

    private val _gameDevelopers = mutableStateMapOf<Int, List<Developer>?>()
    val gameDevelopers: Map<Int, List<Developer>?> = _gameDevelopers

    private val _loadingDevelopers = mutableStateMapOf<Int, Boolean>()
    val loadingDevelopers: Map<Int, Boolean> = _loadingDevelopers

    private val _loadingDescriptions = mutableStateMapOf<Int, Boolean>()
    val loadingDescriptions: Map<Int, Boolean> = _loadingDescriptions

    private val _searchQuery = mutableStateOf("")
    val searchQuery: State<String> = _searchQuery

    init {
        viewModelScope.launch {
            repository.fetchGames()
        }
    }

    fun getGameDetails(id: Int) {
        viewModelScope.launch {
            repository.getGameById(id)
        }
    }

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
        viewModelScope.launch {
            repository.searchGames(query)
        }
    }

    fun getGameDescription(gameId: Int) {
        if (_gameDescriptions.containsKey(gameId) || _loadingDescriptions[gameId] == true) {
            return
        }

        _loadingDescriptions[gameId] = true

        viewModelScope.launch {
            try {
                val details = repository.getGameDetailsOnce(gameId)
                _gameDescriptions[gameId] = details?.description
            } catch (e: Exception) {
                Log.e("GameViewModel", "Error loading game $gameId description", e)
                _gameDescriptions[gameId] = null
            } finally {
                _loadingDescriptions[gameId] = false
            }
        }
    }

    fun getGameDevelopers(gameId: Int) {
        if (_gameDevelopers.containsKey(gameId) || _loadingDevelopers[gameId] == true) {
            return
        }

        _loadingDevelopers[gameId] = true

        viewModelScope.launch {
            try {
                val details = repository.getGameDetailsOnce(gameId)
                _gameDevelopers[gameId] = details?.developers
            } catch (e: Exception) {
                Log.e("GameViewModel", "Error loading game $gameId developers", e)
                _gameDevelopers[gameId] = null
            } finally {
                _loadingDevelopers[gameId] = false
            }
        }
    }
}
