package com.example.consecutivepractice

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import androidx.lifecycle.ViewModel

class GameViewModel : ViewModel() {
    private val repository = GameRepository()

    val games_list: State<List<Game>> = repository.games

    private val _searchQuery = mutableStateOf("")
    val searchQuery: State<String> = _searchQuery

    fun getGameById(id: String): Game? {
        return repository.getGameById(id)
    }

    fun onSearchQueryChanged(query: String) {
        _searchQuery.value = query
        repository.searchGames(query)
    }
}
