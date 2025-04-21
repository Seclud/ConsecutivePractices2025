package com.example.consecutivepractice

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.PaddingValues
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.consecutivepractice.models.Developer
import com.example.consecutivepractice.models.Game
import kotlinx.coroutines.launch

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritesScreen(
    viewModel: FavoritesViewModel = viewModel(),
    onBackClick: () -> Unit,
    onGameClick: (String) -> Unit
) {
    // Collect UI state from the viewModel
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Favorites") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Back"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        when {
            uiState.isLoading -> {
                LoadingScreen(paddingValues)
            }

            uiState.favorites.isEmpty() -> {
                EmptyFavoritesScreen(paddingValues)
            }

            else -> {
                FavoritesList(
                    favorites = uiState.favorites,
                    paddingValues = paddingValues,
                    onGameClick = onGameClick,
                    favoritesViewModel = viewModel
                )
            }
        }
    }
}

@Composable
private fun LoadingScreen(paddingValues: PaddingValues) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues),
        contentAlignment = Alignment.Center
    ) {
        CircularProgressIndicator()
    }
}

@Composable
private fun EmptyFavoritesScreen(paddingValues: PaddingValues) {
    Box(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues),
        contentAlignment = Alignment.Center
    ) {
        Text("You don't have any favorite games yet")
    }
}

@Composable
private fun FavoritesList(
    favorites: List<Game>,
    paddingValues: PaddingValues,
    onGameClick: (String) -> Unit,
    favoritesViewModel: FavoritesViewModel
) {
    LazyColumn(
        modifier = Modifier
            .fillMaxSize()
            .padding(paddingValues),
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(favorites) { game ->
            FavoriteGameCard(
                game = game,
                onClick = { onGameClick(game.id.toString()) },
                favoritesViewModel = favoritesViewModel
            )
        }
    }
}

@Composable
fun FavoriteGameCard(
    game: Game,
    onClick: () -> Unit,
    favoritesViewModel: FavoritesViewModel
) {
    val coroutineScope = rememberCoroutineScope()

    var description by remember { mutableStateOf<String?>(null) }
    var developers by remember { mutableStateOf<List<Developer>?>(null) }
    var isLoadingDescription by remember { mutableStateOf(true) }
    var isLoadingDevelopers by remember { mutableStateOf(true) }

    LaunchedEffect(game.id) {
        coroutineScope.launch {
            description = favoritesViewModel.getFavoriteGameDescription(game.id)
            developers = favoritesViewModel.getFavoriteGameDevelopers(game.id)

            isLoadingDescription = false
            isLoadingDevelopers = false
        }
    }

    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        Column(modifier = Modifier.padding(16.dp)) {
            Row {
                AsyncImage(
                    model = ImageRequest.Builder(LocalContext.current)
                        .data(game.background_image)
                        .crossfade(true)
                        .build(),
                    contentDescription = null,
                    modifier = Modifier.size(100.dp)
                )

                Spacer(modifier = Modifier.width(8.dp))

                Column {
                    Text(
                        text = game.name,
                        style = MaterialTheme.typography.titleLarge
                    )

                    val platformsText =
                        game.platforms?.joinToString(", ") { it.platform.name } ?: "Unknown"
                    Text(
                        text = "Platforms: $platformsText",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))

            when {
                isLoadingDescription -> Text(
                    text = "Loading...",
                    style = MaterialTheme.typography.bodySmall
                )

                description != null -> HtmlText(
                    html = description!!,
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 2
                )

                else -> Text(
                    text = "No description available",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error
                )
            }

            Spacer(modifier = Modifier.height(8.dp))

            // Developer info
            val developerText = when {
                isLoadingDevelopers -> "Loading..."
                developers.isNullOrEmpty() -> "Developer: Unknown"
                else -> "Developer: ${developers!!.joinToString(", ") { it.name }}"
            }
            Text(
                text = developerText,
                style = MaterialTheme.typography.bodySmall
            )

            Spacer(modifier = Modifier.height(8.dp))

            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "Rating: ${game.rating}",
                    style = MaterialTheme.typography.bodySmall
                )

                Text(
                    text = "Release date: ${game.released ?: "N/A"}",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}