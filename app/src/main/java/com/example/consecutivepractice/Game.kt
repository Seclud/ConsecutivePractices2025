package com.example.consecutivepractice

data class Game(
    val id: String,
    val title: String,
    val description: String,
    val releaseDate: String,
    val rating: Double,
    val coverImage: String? = null,
    val genres: List<String> = emptyList(),
    val developer: String = "",
    val platform: List<String> = emptyList(),
    val systemRequirements: SystemRequirements? = null
)

data class SystemRequirements(
    val minimum: Map<String, String> = emptyMap(),
    val recommended: Map<String, String> = emptyMap()
)
