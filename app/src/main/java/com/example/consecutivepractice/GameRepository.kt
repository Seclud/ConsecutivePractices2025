package com.example.consecutivepractice

import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf


class GameRepository {
    private val _games = mutableStateOf(GamesData.games.value)
    val games: State<List<Game>> = _games

    fun getGameById(id: String): Game? {
        return games.value.find { it.id == id }
    }

    fun searchGames(query: String) {
        if (query.isEmpty()) {
            _games.value = GamesData.games.value
        } else {
            _games.value = GamesData.games.value.filter {
                it.title.contains(query, ignoreCase = true)
            }
        }
    }
}
