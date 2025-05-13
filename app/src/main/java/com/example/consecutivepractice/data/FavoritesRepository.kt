package com.example.consecutivepractice.data

import com.example.consecutivepractice.data.room.FavoriteGame
import com.example.consecutivepractice.models.Developer
import com.example.consecutivepractice.models.Game
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.map

class FavoritesRepository(databaseProvider: AppDatabaseProvider) {
    private val favoriteGameDao = databaseProvider.getDatabase().favoriteGameDao()


    val allFavorites: Flow<List<Game>> = favoriteGameDao.getAllFavorites().map { favoriteGames ->
        favoriteGames.map { FavoriteGame.toGame(it) }
    }


    suspend fun getFavoriteGameDescription(gameId: Int): String? {
        return favoriteGameDao.getFavoriteById(gameId)?.description
    }


    suspend fun getFavoriteGameDevelopers(gameId: Int): List<Developer>? {
        return favoriteGameDao.getFavoriteById(gameId)?.developersList
    }


    suspend fun isFavorite(gameId: Int): Boolean {
        return favoriteGameDao.isFavorite(gameId)
    }


    suspend fun addToFavorites(
        game: Game,
        description: String? = null,
        developers: List<Developer>? = null
    ) {
        favoriteGameDao.addFavorite(FavoriteGame.fromGame(game, description, developers))
    }


    suspend fun removeFromFavorites(gameId: Int) {
        favoriteGameDao.removeFavoriteById(gameId)
    }


    suspend fun toggleFavorite(
        game: Game,
        description: String? = null,
        developers: List<Developer>? = null
    ): Boolean {
        val isFavorite = isFavorite(game.id)
        if (isFavorite) {
            removeFromFavorites(game.id)
        } else {
            addToFavorites(game, description, developers)
        }
        return !isFavorite
    }
}