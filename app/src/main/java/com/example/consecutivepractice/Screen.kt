package com.example.consecutivepractice

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Home
import androidx.compose.material.icons.automirrored.filled.List
import androidx.compose.ui.graphics.vector.ImageVector

sealed class Screen(val route: String, val title: String, val icon: ImageVector) {
    object Games : Screen("games", "Games", Icons.Default.Home)
    object Screen1 : Screen("screen1", "Screen 1", Icons.AutoMirrored.Filled.List)
    object Screen2 : Screen("screen2", "Screen 2", Icons.AutoMirrored.Filled.List)
    object Screen3 : Screen("screen3", "Screen 3", Icons.AutoMirrored.Filled.List)

    companion object {
        fun createRoute(gameId: String) = "game/$gameId"
    }
}
