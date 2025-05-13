package com.example.consecutivepractice.screens

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.Arrangement
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
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.consecutivepractice.components.EmptyStateMessage
import com.example.consecutivepractice.models.Game
import com.example.consecutivepractice.viewmodels.FavoriteGameDetails
import com.example.consecutivepractice.viewmodels.FavoritesViewModel
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FavoritesScreen(
    viewModel: FavoritesViewModel = koinViewModel(),
    onBackClick: () -> Unit,
    onGameClick: (String) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Избранное") },
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
            }            else -> {
                FavoritesList(
                    favorites = uiState.favorites,
                    paddingValues = paddingValues,
                    onGameClick = onGameClick,
                    gameDetailsCache = uiState.gameDetailsCache,
                    loadingGameDetails = uiState.loadingGameDetails
                )
            }
        }
    }
}

@Composable
private fun LoadingScreen(paddingValues: PaddingValues) {
    com.example.consecutivepractice.components.LoadingIndicator(contentPadding = paddingValues)
}

@Composable
private fun EmptyFavoritesScreen(paddingValues: PaddingValues) {
    EmptyStateMessage(
        message = "Вы еще не выбрали избранные игры",
        contentPadding = paddingValues
    )
}

@Composable
private fun FavoritesList(
    favorites: List<Game>,
    paddingValues: PaddingValues,
    onGameClick: (String) -> Unit,
    gameDetailsCache: Map<Int, FavoriteGameDetails>,
    loadingGameDetails: Set<Int>
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
                gameDetailsCache = gameDetailsCache,
                loadingGameDetails = loadingGameDetails
            )
        }
    }
}

@Composable
fun FavoriteGameCard(
    game: Game,
    onClick: () -> Unit,
    gameDetailsCache: Map<Int, FavoriteGameDetails>,
    loadingGameDetails: Set<Int>
) {
    val gameDetails = gameDetailsCache[game.id]
    val isLoading = loadingGameDetails.contains(game.id) || gameDetails == null

    val description = gameDetails?.description
    val developers = gameDetails?.developers

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
                        game.platforms?.joinToString(", ") { it.platform.name } ?: "Неизвестно"
                    Text(
                        text = "Платформы: $platformsText",
                        style = MaterialTheme.typography.bodySmall
                    )
                }
            }

            Spacer(modifier = Modifier.height(8.dp))
            when {
                isLoading -> Text(
                    text = "Загрузка...",
                    style = MaterialTheme.typography.bodySmall
                )

                description != null -> HtmlText(
                    html = description,
                    modifier = Modifier.fillMaxWidth(),
                    maxLines = 2
                )

                else -> Text(
                    text = "Описание недоступно",
                    style = MaterialTheme.typography.bodySmall,
                    color = MaterialTheme.colorScheme.error
                )
            }

            Spacer(modifier = Modifier.height(8.dp))


            val developerText = when {
                isLoading -> "Загрузка..."
                developers.isNullOrEmpty() -> "Разработчик: Неизвестно"
                else -> "Разработчик: ${developers.joinToString(", ") { it.name }}"
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
                    text = "Рейтинг: ${game.rating}",
                    style = MaterialTheme.typography.bodySmall
                )

                Text(
                    text = "Дата выхода: ${game.released ?: "N/A"}",
                    style = MaterialTheme.typography.bodySmall
                )
            }
        }
    }
}