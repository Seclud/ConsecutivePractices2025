package com.example.consecutivepractice.repositories

import android.util.Log
import com.example.consecutivepractice.api.GamesApi
import com.example.consecutivepractice.models.Game
import com.example.consecutivepractice.models.GameDetailsResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class GameRepository(
    private val api: GamesApi
) {
    suspend fun fetchGames(page: Int = 2, pageSize: Int = 20, search: String? = null): List<Game> {
        return withContext(Dispatchers.IO) {
            try {
                val response = api.getGames(page = page, pageSize = pageSize, search = search)
                if (response.isSuccessful) {
                    response.body()?.results ?: emptyList()
                } else {
                    Log.e("GameRepository", "Ошибка: ${response.code()} - ${response.message()}")
                    emptyList()
                }
            } catch (e: Exception) {
                Log.e("GameRepository", "Ошибка: ${e.message}")
                emptyList()
            }
        }
    }

    suspend fun searchGames(query: String): List<Game> {
        return if (query.isEmpty()) {
            fetchGames()
        } else {
            fetchGames(1, 20, query)
        }
    }

    suspend fun getGameDetailsOnce(id: Int): GameDetailsResponse? {
        return withContext(Dispatchers.IO) {
            try {
                val response = api.getGameDetails(id)
                if (response.isSuccessful && response.body() != null) {
                    response.body()
                } else {
                    val errorBody = response.errorBody()?.string() ?: "Неизвестная ошибка"
                    Log.e("GameRepository", "Ошибка ${response.code()} в игре $id: $errorBody")
                    null
                }
            } catch (e: Exception) {
                Log.e("GameRepository", "Ошибка загрузки игры $id", e)
                null
            }
        }
    }
}
