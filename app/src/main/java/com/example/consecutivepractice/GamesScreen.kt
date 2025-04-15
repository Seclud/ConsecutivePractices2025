package com.example.consecutivepractice

import android.util.Log
import android.widget.TextView
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
import androidx.compose.material.icons.filled.Clear
import androidx.compose.material.icons.filled.Search
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.CircularProgressIndicator
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedTextField
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.unit.dp
import androidx.compose.ui.viewinterop.AndroidView
import androidx.core.text.HtmlCompat
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest
import com.example.consecutivepractice.models.Developer
import com.example.consecutivepractice.models.Game

@Composable
fun GamesScreen(
    viewModel: GameViewModel = viewModel(),
    onGameClick: (String) -> Unit
) {
    val games = viewModel.gamesList.value
    val isLoading = viewModel.loading.value
    val error = viewModel.error.value
    val searchQuery = viewModel.searchQuery.value

    Column(modifier = Modifier.fillMaxSize()) {
        SearchBar(searchQuery, viewModel::onSearchQueryChanged)

        when {
            isLoading -> LoadingIndicator()
            error != null -> ErrorMessage(error)
            games.isEmpty() -> EmptyStateMessage("Игр не найдено")
            else -> GamesList(games, onGameClick, viewModel)
        }
    }
}

@Composable
private fun SearchBar(query: String, onQueryChanged: (String) -> Unit) {
    OutlinedTextField(
        value = query,
        onValueChange = onQueryChanged,
        modifier = Modifier
            .fillMaxWidth()
            .padding(16.dp),
        label = { Text("Поиск игр") },
        singleLine = true,
        leadingIcon = {
            Icon(imageVector = Icons.Default.Search, contentDescription = "Поиск")
        },
        trailingIcon = {
            if (query.isNotEmpty()) {
                IconButton(onClick = { onQueryChanged("") }) {
                    Icon(
                        imageVector = Icons.Default.Clear,
                        contentDescription = "Clear search"
                    )
                }
            }
        }
    )
}

@Composable
private fun LoadingIndicator() {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        CircularProgressIndicator()
    }
}

@Composable
private fun ErrorMessage(message: String) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text("Error: $message")
    }
}

@Composable
private fun EmptyStateMessage(message: String) {
    Box(modifier = Modifier.fillMaxSize(), contentAlignment = Alignment.Center) {
        Text(message)
    }
}

@Composable
private fun GamesList(games: List<Game>, onGameClick: (String) -> Unit, viewModel: GameViewModel) {
    LazyColumn(
        contentPadding = PaddingValues(16.dp),
        verticalArrangement = Arrangement.spacedBy(16.dp)
    ) {
        items(games) { game ->
            GameCard(
                game = game,
                onClick = { onGameClick(game.id.toString()) },
                viewModel = viewModel
            )
        }
    }
}

@Composable
fun GameCard(game: Game, onClick: () -> Unit, viewModel: GameViewModel) {
    Card(
        modifier = Modifier
            .fillMaxWidth()
            .clickable(onClick = onClick),
        elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
    ) {
        LaunchedEffect(game.id) {
            viewModel.getGameDescription(game.id)
            viewModel.getGameDevelopers(game.id)
        }

        val description = viewModel.gameDescriptions[game.id]
        val developers = viewModel.gameDevelopers[game.id]
        val isLoadingDescription = viewModel.loadingDescriptions[game.id] == true
        val isLoadingDevelopers = viewModel.loadingDevelopers[game.id] == true

        Log.e("GameScreenDevelopers of game ${game.id}", developers.toString())

        Column(modifier = Modifier.padding(16.dp)) {
            GameHeader(game)

            Spacer(modifier = Modifier.height(8.dp))

            GameDescription(description, isLoadingDescription)

            Spacer(modifier = Modifier.height(8.dp))

            GameDeveloperInfo(developers, isLoadingDevelopers)

            Spacer(modifier = Modifier.height(8.dp))

            GameFooter(game)
        }
    }
}

@Composable
private fun GameHeader(game: Game) {
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

            val platformsText = game.platforms?.joinToString(", ") { it.platform.name } ?: "Unknown"
            Text(
                text = "Платформы: $platformsText",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Composable
private fun GameDescription(description: String?, isLoading: Boolean) {
    when {
        isLoading -> LoadingText("Загрузка...")
        description != null -> HtmlText(
            html = description,
            modifier = Modifier.fillMaxWidth(),
            maxLines = 2
        )

        else -> Text(
            text = "Описание отсутствует",
            style = MaterialTheme.typography.bodySmall,
            color = MaterialTheme.colorScheme.error
        )
    }
}

@Composable
private fun GameDeveloperInfo(developers: List<Developer>?, isLoading: Boolean) {
    val text = when {
        isLoading -> "Загрузка..."
        developers.isNullOrEmpty() -> "Разработчик: Неизвестно"
        else -> "Разработчик: ${developers.joinToString(", ") { it.name }}"
    }

    Text(
        text = text,
        style = MaterialTheme.typography.bodySmall
    )
}

@Composable
private fun GameFooter(game: Game) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.SpaceBetween
    ) {
        Text(
            text = "Оценка: ${game.rating}",
            style = MaterialTheme.typography.bodySmall
        )

        Text(
            text = "Дата выхода: ${game.released ?: "N/A"}",
            style = MaterialTheme.typography.bodySmall
        )
    }
}

@Composable
private fun LoadingText(text: String) {
    Text(
        text = text,
        style = MaterialTheme.typography.bodySmall
    )
}

@Composable
fun HtmlText(
    html: String,
    modifier: Modifier = Modifier,
    maxLines: Int = Int.MAX_VALUE
) {
    AndroidView(
        modifier = modifier,
        factory = { context ->
            TextView(context).apply {
                this.maxLines = maxLines
            }
        },
        update = { textView ->
            val spanned = HtmlCompat.fromHtml(html, HtmlCompat.FROM_HTML_MODE_COMPACT)
            textView.text = spanned
        }
    )
}