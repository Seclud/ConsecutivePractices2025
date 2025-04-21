package com.example.consecutivepractice.data.room

import androidx.room.Entity
import androidx.room.PrimaryKey
import com.example.consecutivepractice.models.Developer
import com.example.consecutivepractice.models.Game
import com.example.consecutivepractice.models.Genre
import com.example.consecutivepractice.models.PlatformWrapper

@Entity(tableName = "favorite_games")
data class FavoriteGame(
    @PrimaryKey val id: Int,
    val name: String,
    val background_image: String?,
    val rating: Double,
    val released: String?,
    val description: String? = null,

    val genresList: List<Genre>? = null,
    val platformsList: List<PlatformWrapper>? = null,
    val developersList: List<Developer>? = null,
    val dateAdded: Long = System.currentTimeMillis()
) {
    companion object {
        fun fromGame(
            game: Game,
            description: String? = null,
            developers: List<Developer>? = null
        ): FavoriteGame {
            return FavoriteGame(
                id = game.id,
                name = game.name,
                background_image = game.background_image,
                rating = game.rating,
                released = game.released,
                description = description,
                genresList = game.genres,
                platformsList = game.platforms,
                developersList = developers
            )
        }

        fun toGame(favoriteGame: FavoriteGame): Game {
            return Game(
                id = favoriteGame.id,
                name = favoriteGame.name,
                background_image = favoriteGame.background_image,
                rating = favoriteGame.rating,
                released = favoriteGame.released,
                genres = favoriteGame.genresList,
                platforms = favoriteGame.platformsList
            )
        }
    }
}