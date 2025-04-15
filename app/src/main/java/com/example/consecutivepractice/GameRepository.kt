package com.example.consecutivepractice

import android.util.Log
import androidx.compose.runtime.State
import androidx.compose.runtime.mutableStateOf
import com.example.consecutivepractice.api.GamesApi
import com.example.consecutivepractice.models.Game
import com.example.consecutivepractice.models.GameDetailsResponse
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext

class GameRepository(
    private val api: GamesApi
) {
    private val _games = mutableStateOf<List<Game>>(emptyList())
    val games: State<List<Game>> = _games

    private val _loading = mutableStateOf(false)
    val loading: State<Boolean> = _loading

    private val _error = mutableStateOf<String?>(null)
    val error: State<String?> = _error

    private val _gameDetails = mutableStateOf<GameDetailsResponse?>(null)
    val gameDetails: State<GameDetailsResponse?> = _gameDetails

    private val _detailsLoading = mutableStateOf(false)
    val detailsLoading: State<Boolean> = _detailsLoading

    private val _detailsError = mutableStateOf<String?>(null)
    val detailsError: State<String?> = _detailsError

    suspend fun fetchGames(page: Int = 2, pageSize: Int = 20) {
        fetchGamesInternal(page, pageSize, null)
    }

    suspend fun searchGames(query: String) {
        if (query.isEmpty()) {
            fetchGames()
        } else {
            fetchGamesInternal(1, 20, query)
        }
    }

    private suspend fun fetchGamesInternal(page: Int, pageSize: Int, search: String?) {
        _loading.value = true
        _error.value = null

        try {
            val response = withContext(Dispatchers.IO) {
                api.getGames(page = page, pageSize = pageSize, search = search)
            }

            if (response.isSuccessful) {
                response.body()?.let { gamesResponse ->
                    _games.value = gamesResponse.results
                } ?: run {
                    _error.value = "Пустой ответ"
                }
            } else {
                _error.value = "Ошибка: ${response.code()} - ${response.message()}"
            }
        } catch (e: Exception) {
            _error.value = "Ошибка: ${e.message}"
        } finally {
            _loading.value = false
        }
    }

    suspend fun getGameById(id: Int) {
        _detailsLoading.value = true
        _detailsError.value = null
        _gameDetails.value = null

        try {
            val response = withContext(Dispatchers.IO) {
                api.getGameDetails(id)
            }

            handleGameDetailsResponse(response, id)
        } catch (e: Exception) {
            _detailsError.value = "Ошибка: ${e.message}"
            Log.e("GameRepository", "Ошибка загрузки игры $id", e)
        } finally {
            _detailsLoading.value = false
        }
    }

    suspend fun getGameDetailsOnce(id: Int): GameDetailsResponse? {
        try {
            val response = withContext(Dispatchers.IO) {
                api.getGameDetails(id)
            }

            if (response.isSuccessful && response.body() != null) {
                return response.body()
            } else {
                val errorBody = response.errorBody()?.string() ?: "Неизвестная ошибка"
                Log.e("GameRepository", "Ошибка ${response.code()} в игре $id: $errorBody")
            }
        } catch (e: Exception) {
            Log.e("GameRepository", "Ошибка загрузки игрыe $id", e)
        }
        return null
    }

    private fun handleGameDetailsResponse(
        response: retrofit2.Response<GameDetailsResponse>,
        id: Int
    ) {
        if (response.isSuccessful) {
            if (response.body() == null) {
                _detailsError.value = "Пустое описание"
                Log.e("GameRepository", "Описание игры пустое: $id")
            } else {
                _gameDetails.value = response.body()
            }
        } else {
            val errorBody = response.errorBody()?.string() ?: "Неизвестная ошибка"
            _detailsError.value = "Ошибка ${response.code()}: $errorBody"
            Log.e("GameRepository", "Ошибка ${response.code()} для игры $id: $errorBody")
        }
    }
}
