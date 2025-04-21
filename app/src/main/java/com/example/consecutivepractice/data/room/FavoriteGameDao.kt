package com.example.consecutivepractice.data.room

import androidx.room.Dao
import androidx.room.Delete
import androidx.room.Insert
import androidx.room.OnConflictStrategy
import androidx.room.Query
import kotlinx.coroutines.flow.Flow

@Dao
interface FavoriteGameDao {
    @Query("SELECT * FROM favorite_games ORDER BY dateAdded DESC")
    fun getAllFavorites(): Flow<List<FavoriteGame>>

    @Query("SELECT * FROM favorite_games WHERE id = :gameId")
    suspend fun getFavoriteById(gameId: Int): FavoriteGame?

    @Insert(onConflict = OnConflictStrategy.REPLACE)
    suspend fun addFavorite(favoriteGame: FavoriteGame)

    @Delete
    suspend fun removeFavorite(favoriteGame: FavoriteGame)

    @Query("DELETE FROM favorite_games WHERE id = :gameId")
    suspend fun removeFavoriteById(gameId: Int)

    @Query("SELECT EXISTS(SELECT 1 FROM favorite_games WHERE id = :gameId)")
    suspend fun isFavorite(gameId: Int): Boolean
}