package com.example.consecutivepractice.domain

import com.example.consecutivepractice.models.Game
import com.example.consecutivepractice.repositories.FilterRepository
import java.time.LocalDate
import java.time.format.DateTimeFormatter
import java.time.format.DateTimeParseException

class GameFilterUseCase(private val filterRepository: FilterRepository) {

    private val twoYearsAgo = LocalDate.now().minusYears(2)

    fun filterGames(games: List<Game>): List<Game> {
        val settings = filterRepository.loadFilters()

        return games.filter { game ->
            val passesRatingFilter = game.rating >= settings.minRating

            val passesGenreFilter = if (settings.genre == FilterRepository.DEFAULT_GENRE) {
                true
            } else {
                game.genres?.any { it.name.equals(settings.genre, ignoreCase = true) } ?: false
            }

            val passesDateFilter = if (!settings.onlyRecent) {
                true
            } else {
                val parsedDate = parseDate(game.released)
                parsedDate != null && parsedDate.isAfter(twoYearsAgo)
            }

            passesRatingFilter && passesGenreFilter && passesDateFilter
        }
    }

    private fun parseDate(dateString: String?): LocalDate? {
        if (dateString.isNullOrEmpty()) return null

        return try {
            LocalDate.parse(dateString, DateTimeFormatter.ISO_DATE)
        } catch (e: DateTimeParseException) {
            null
        }
    }
}
