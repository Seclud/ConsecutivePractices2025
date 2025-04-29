package com.example.consecutivepractice

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Icon
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.NavigationBarItem
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.navigation.NavHostController
import androidx.navigation.NavType
import androidx.navigation.compose.NavHost
import androidx.navigation.compose.composable
import androidx.navigation.compose.currentBackStackEntryAsState
import androidx.navigation.compose.rememberNavController
import androidx.navigation.navArgument
import com.example.consecutivepractice.screens.FavoritesScreen
import com.example.consecutivepractice.screens.GameDetailsScreen
import com.example.consecutivepractice.screens.GamesScreen
import com.example.consecutivepractice.screens.PlaceholderScreen
import com.example.consecutivepractice.screens.ProfileEditScreen
import com.example.consecutivepractice.screens.ProfileScreen
import com.example.consecutivepractice.ui.theme.ConsecutivePracticeTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        setContent {
            ConsecutivePracticeTheme {
                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    val navController = rememberNavController()
                    NavGraph(navController = navController)
                }
            }
        }
    }
}

@Composable
fun NavGraph(navController: NavHostController) {
    Scaffold(
        bottomBar = {
            BottomNavBar(navController = navController)
        }
    ) { innerPadding ->
        NavHost(
            navController = navController,
            startDestination = Screen.Games.route,
            modifier = Modifier.padding(innerPadding)
        ) {
            composable(Screen.Games.route) {
                GamesScreen(
                    onGameClick = { gameId ->
                        navController.navigate(Screen.createRoute(gameId))
                    }
                )
            }
            composable(Screen.Favorites.route) {
                FavoritesScreen(
                    onBackClick = {
                        navController.navigate(Screen.Games.route) {
                            popUpTo(navController.graph.startDestinationId)
                            launchSingleTop = true
                        }
                    },
                    onGameClick = { gameId ->
                        navController.navigate(Screen.createRoute(gameId))
                    }
                )
            }
            composable(Screen.Profile.route) {
                ProfileScreen(
                    onEditClick = {
                        navController.navigate(Screen.PROFILE_EDIT_ROUTE)
                    }
                )
            }
            composable(Screen.PROFILE_EDIT_ROUTE) {
                ProfileEditScreen(
                    onBackClick = {
                        navController.popBackStack()
                    },
                    onSaveComplete = {
                        navController.popBackStack()
                    }
                )
            }
            composable(Screen.Screen1.route) {
                PlaceholderScreen(screenName = "Screen 1")
            }
            composable(
                route = "game/{gameId}",
                arguments = listOf(navArgument("gameId") { type = NavType.StringType })
            ) { backStackEntry ->
                val gameId = backStackEntry.arguments?.getString("gameId") ?: ""
                GameDetailsScreen(
                    gameId = gameId,
                    onBackClick = {
                        navController.popBackStack()
                    }
                )
            }
        }
    }
}

@Composable
fun BottomNavBar(navController: NavHostController) {
    val items = listOf(
        Screen.Games,
        Screen.Favorites,
        Screen.Screen1,
        Screen.Profile,
    )

    androidx.compose.material3.NavigationBar {
        val navBackStackEntry by navController.currentBackStackEntryAsState()
        val currentRoute = navBackStackEntry?.destination?.route

        items.forEach { screen ->
            NavigationBarItem(
                icon = { Icon(screen.icon, contentDescription = null) },
                label = { Text(screen.title) },
                selected = currentRoute == screen.route,
                onClick = {
                    if (currentRoute != screen.route) {
                        navController.navigate(screen.route) {
                            popUpTo(navController.graph.startDestinationId)
                            launchSingleTop = true
                        }
                    }
                }
            )
        }
    }
}