package com.example.consecutivepractice.models

data class GamesListResponse(
    val count: Int,
    val next: String?,
    val previous: String?,
    val results: List<Game>,
)

data class Game(
    val id: Int,
    val name: String,
    val background_image: String?,
    val rating: Double,
    val released: String?,
    val genres: List<Genre>?,
    val platforms: List<PlatformWrapper>?
)

data class Genre(
    val id: Int,
    val name: String
)

data class Platform(
    val id: Int,
    val name: String,
    val slug: String?,
    val image: String?,
    val year_end: Int?,
    val year_start: Int?,
    val games_count: Int?,
    val image_background: String?,
)

data class GameDetailsResponse(
    val id: Int,
    val name: String,
    val description: String?,
    val background_image: String?,
    val rating: Double,
    val released: String?,
    val genres: List<Genre>?,
    val platforms: List<PlatformWrapper>?,
    val developers: List<Developer>?,
)

data class Developer(
    val id: Int,
    val name: String,
)

data class PlatformWrapper(
    val platform: Platform,
    val released_at: String?,
    val requirements: SystemRequirements?
)

data class SystemRequirements(
    val minimum: String?,
    val recommended: String?,
)
