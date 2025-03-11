package com.example.consecutivepractice

import androidx.compose.foundation.background
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Star
import androidx.compose.material.icons.automirrored.filled.ArrowBack
import com.example.consecutivepractice.ui.Gamepad
import com.example.consecutivepractice.ui.Computer
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.constraintlayout.compose.ConstraintLayout
import androidx.constraintlayout.compose.Dimension
import androidx.lifecycle.viewmodel.compose.viewModel
import coil.compose.AsyncImage
import coil.request.ImageRequest

@OptIn(ExperimentalLayoutApi::class, ExperimentalMaterial3Api::class)
@Composable
fun GameDetailsScreen(
    gameId: String,
    onBackClick: () -> Unit = {},
    viewModel: GameViewModel = viewModel()
) {
    val game = viewModel.getGameById(gameId)
    val scrollState = rememberScrollState()

    game?.let {
        Scaffold(
            topBar = {
                TopAppBar(
                    title = { Text(it.title, maxLines = 1) },
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
            Column(
                modifier = Modifier
                    .padding(paddingValues)
                    .verticalScroll(scrollState)
            ) {
                Box(
                    modifier = Modifier
                        .fillMaxWidth()
                        .height(250.dp)
                        .background(Color.Red)
                ) {
                    AsyncImage(
                        model = ImageRequest.Builder(LocalContext.current)
                            .data(it.coverImage)
                            .crossfade(true)
                            .build(),
                        contentDescription = "${it.title} cover",
                        modifier = Modifier.fillMaxSize(),
                        contentScale = androidx.compose.ui.layout.ContentScale.Crop
                    )

                    // Add a scrim for better text readability
                    Box(
                        modifier = Modifier
                            .fillMaxSize()
                            .background(
                                androidx.compose.ui.graphics.Brush.verticalGradient(
                                    colors = listOf(
                                        Color.Transparent,
                                        Color.Black.copy(alpha = 0.7f)
                                    )
                                )
                            )
                    )

                    Box(
                        modifier = Modifier
                            .matchParentSize()
                            .padding(16.dp),
                        contentAlignment = Alignment.BottomStart
                    ) {
                        Column {
                            Text(
                                text = it.title,
                                style = MaterialTheme.typography.headlineMedium,
                                fontWeight = FontWeight.Bold,
                                color = Color.White
                            )

                            Text(
                                text = "${it.developer} • ${it.releaseDate}",
                                style = MaterialTheme.typography.bodyMedium,
                                color = Color.White
                            )
                        }
                    }
                }

                ConstraintLayout(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    val (ratingBox, genreChips) = createRefs()

                    Surface(
                        modifier = Modifier
                            .size(80.dp)
                            .constrainAs(ratingBox) {
                                top.linkTo(parent.top)
                                start.linkTo(parent.start)
                                bottom.linkTo(parent.bottom)
                            },
                        shape = RoundedCornerShape(8.dp),
                        color = MaterialTheme.colorScheme.primaryContainer
                    ) {
                        Column(
                            horizontalAlignment = Alignment.CenterHorizontally,
                            verticalArrangement = Arrangement.Center
                        ) {
                            Icon(
                                imageVector = Icons.Default.Star,
                                contentDescription = null,
                                tint = Color.Yellow
                            )
                            Text(
                                text = "${it.rating}/10",
                                style = MaterialTheme.typography.bodyLarge,
                                fontWeight = FontWeight.Bold
                            )
                        }
                    }

                    FlowRow(
                        modifier = Modifier
                            .constrainAs(genreChips) {
                                top.linkTo(parent.top)
                                start.linkTo(ratingBox.end, margin = 16.dp)
                                end.linkTo(parent.end)
                                bottom.linkTo(parent.bottom)
                                width = Dimension.fillToConstraints
                            },
                        horizontalArrangement = Arrangement.spacedBy(8.dp)
                    ) {
                        it.genres.take(3).forEach { genre ->
                            SuggestionChip(
                                onClick = { },
                                label = { Text(genre) }
                            )
                        }
                    }
                }

                // Platforms Section
                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(horizontal = 16.dp, vertical = 8.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier.padding(16.dp)
                    ) {
                        Text(
                            text = "Доступные платформы",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        FlowRow(
                            modifier = Modifier.fillMaxWidth(),
                            horizontalArrangement = Arrangement.spacedBy(8.dp)
                        ) {
                            it.platform.forEach { platform ->
                                AssistChip(
                                    onClick = { },
                                    label = { Text(platform) },
                                    leadingIcon = {
                                        Icon(
                                            imageVector = Gamepad,
                                            contentDescription = null,
                                            Modifier.size(18.dp)
                                        )
                                    }
                                )
                            }
                        }
                    }
                }

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(modifier = Modifier.padding(16.dp)) {
                        Text(
                            text = "Описание",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = it.description,
                            style = MaterialTheme.typography.bodyLarge
                        )
                    }
                }

                ConstraintLayout(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp)
                ) {
                    val (criticsTitle, userTitle, criticsScore, userScore, divider) = createRefs()
                    val barrier = createEndBarrier(criticsTitle, userTitle)

                    Text(
                        text = "Оценка критиков:",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.constrainAs(criticsTitle) {
                            top.linkTo(parent.top)
                            start.linkTo(parent.start)
                        }
                    )

                    Text(
                        text = "${(it.rating * 10).toInt()}/100",
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.End,
                        modifier = Modifier.constrainAs(criticsScore) {
                            top.linkTo(criticsTitle.top)
                            bottom.linkTo(criticsTitle.bottom)
                            start.linkTo(barrier, margin = 16.dp)
                        }
                    )

                    HorizontalDivider(
                        modifier = Modifier.constrainAs(divider) {
                            top.linkTo(criticsTitle.bottom, margin = 8.dp)
                            start.linkTo(parent.start)
                            end.linkTo(parent.end)
                        }
                    )

                    Text(
                        text = "Оценка игроков:",
                        style = MaterialTheme.typography.bodyLarge,
                        fontWeight = FontWeight.Bold,
                        modifier = Modifier.constrainAs(userTitle) {
                            top.linkTo(divider.bottom, margin = 8.dp)
                            start.linkTo(parent.start)
                        }
                    )

                    Text(
                        text = "${(it.rating * 8).toInt()}/100",
                        style = MaterialTheme.typography.bodyLarge,
                        textAlign = TextAlign.End,
                        modifier = Modifier.constrainAs(userScore) {
                            top.linkTo(userTitle.top)
                            bottom.linkTo(userTitle.bottom)
                            start.linkTo(barrier, margin = 16.dp)
                        }
                    )
                }

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "Информация о разработчике",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(16.dp))

                        DeveloperInfoItem(
                            name = it.developer,
                            role = "Разработчик"
                        )
                    }
                }

                Card(
                    modifier = Modifier
                        .fillMaxWidth()
                        .padding(16.dp),
                    elevation = CardDefaults.cardElevation(defaultElevation = 4.dp)
                ) {
                    Column(
                        modifier = Modifier
                            .fillMaxWidth()
                            .padding(16.dp)
                    ) {
                        Text(
                            text = "Системные требования",
                            style = MaterialTheme.typography.titleLarge,
                            fontWeight = FontWeight.Bold
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        if (it.systemRequirements != null) {
                            Column {
                                if (it.systemRequirements.minimum.isNotEmpty()) {
                                    Text(
                                        text = "Минимальные:",
                                        fontWeight = FontWeight.Bold,
                                        style = MaterialTheme.typography.bodyMedium
                                    )

                                    it.systemRequirements.minimum.forEach { (key, value) ->
                                        Text("$key: $value")
                                    }

                                    Spacer(modifier = Modifier.height(8.dp))
                                }

                                if (it.systemRequirements.recommended.isNotEmpty()) {
                                    Text(
                                        text = "Рекомендованные:",
                                        fontWeight = FontWeight.Bold,
                                        style = MaterialTheme.typography.bodyMedium
                                    )

                                    it.systemRequirements.recommended.forEach { (key, value) ->
                                        Text("$key: $value")
                                    }
                                }
                            }
                        } else if (it.platform.contains("PC")) {
                            Text("System requirements data not available for this PC game.")
                        } else {
                            Text("System requirements not available for console platforms.")
                        }
                    }
                }

                Spacer(modifier = Modifier.height(80.dp)) // Space for FAB
            }
        }
    } ?: Box(
        modifier = Modifier.fillMaxSize(),
        contentAlignment = Alignment.Center
    ) {
        Text("Game not found")
    }
}

@Composable
fun DeveloperInfoItem(name: String, role: String) {
    Row(
        verticalAlignment = Alignment.CenterVertically,
        modifier = Modifier.fillMaxWidth()
    ) {
        Box(
            modifier = Modifier
                .size(70.dp)
                .clip(CircleShape)
                .background(MaterialTheme.colorScheme.primaryContainer),
            contentAlignment = Alignment.Center
        ) {
            Icon(
                imageVector = Computer,
                contentDescription = null,
                modifier = Modifier.size(36.dp),
                tint = MaterialTheme.colorScheme.onPrimaryContainer
            )
        }

        Spacer(modifier = Modifier.width(16.dp))

        Column {
            Text(
                text = name,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold
            )

            Text(
                text = role,
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}
