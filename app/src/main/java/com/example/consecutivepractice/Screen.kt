package com.example.consecutivepractice

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.material.icons.filled.Favorite
import androidx.compose.material.icons.filled.Home
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    object Games : Screen("games", "Игры", Icons.Default.Home)
    object Favorites : Screen("favorites", "Избранное", Icons.Default.Favorite)
    object Screen1 : Screen("screen1", "Screen 1", Icons.AutoMirrored.Filled.List)

    companion object {
        fun createRoute(gameId: String) = "game/$gameId"
    }
}
