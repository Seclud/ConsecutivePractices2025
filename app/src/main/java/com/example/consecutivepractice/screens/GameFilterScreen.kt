package com.example.consecutivepractice.screens

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import androidx.compose.material3.Button
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.OutlinedButton
import androidx.compose.material3.RadioButton
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Slider
import androidx.compose.material3.Switch
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.collectAsState
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp
import com.example.consecutivepractice.viewmodels.GameFilterViewModel
import org.koin.androidx.compose.koinViewModel

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun GameFilterScreen(
    viewModel: GameFilterViewModel = koinViewModel(),
    onBackClick: () -> Unit,
    onApplyFilters: (Float, String, Boolean) -> Unit
) {
    val uiState by viewModel.uiState.collectAsState()

    val genreOptions = listOf(
        "All",
        "Action",
        "Adventure",
        "RPG",
        "Strategy",
        "Sports",
        "Simulation",
        "Racing",
        "Puzzle"
    )

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text("Отфильтровать игры") },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(Icons.AutoMirrored.Filled.ArrowBack, contentDescription = "Back")
                    }
                }
            )
        }
    ) { paddingValues ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(paddingValues)
                .padding(16.dp)
                .verticalScroll(rememberScrollState()),
            verticalArrangement = Arrangement.spacedBy(24.dp)
        ) {
            // рейтинг
            Column {
                Text(
                    text = "Минимальный рейтинг: ${String.format("%.1f", uiState.minRating)}",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))
                Slider(
                    value = uiState.minRating,
                    onValueChange = { viewModel.updateMinRating(it) },
                    valueRange = 0f..5f,
                    steps = 9
                )
            }

            // жанр
            Column {
                Text(
                    text = "Жанр",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold
                )
                Spacer(modifier = Modifier.height(8.dp))

                genreOptions.forEach { genre ->
                    Row(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(vertical = 4.dp),
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        RadioButton(
                            selected = uiState.selectedGenre == genre,
                            onClick = { viewModel.updateSelectedGenre(genre) }
                        )
                        Text(
                            text = genre,
                            modifier = Modifier.padding(start = 8.dp)
                        )
                    }
                }
            }

            // недавние
            Row(
                modifier = Modifier.fillMaxWidth(),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Только недавние игры (Последние 2 года)",
                    style = MaterialTheme.typography.titleMedium,
                    fontWeight = FontWeight.Bold,
                    modifier = Modifier.weight(1f)
                )
                Switch(
                    checked = uiState.onlyRecentGames,
                    onCheckedChange = { viewModel.updateOnlyRecentGames(it) }
                )
            }

            Button(
                onClick = {
                    onApplyFilters(
                        uiState.minRating,
                        uiState.selectedGenre,
                        uiState.onlyRecentGames
                    )
                },
                modifier = Modifier
                    .fillMaxWidth()
                    .padding(vertical = 16.dp)
            ) {
                Text("Применить фильтр")
            }

            OutlinedButton(
                onClick = { viewModel.resetFilters() },
                modifier = Modifier.fillMaxWidth()
            ) {
                Text("Сбросить фильтр")
            }
        }
    }
}