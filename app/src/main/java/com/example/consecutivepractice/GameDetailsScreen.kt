package com.example.consecutivepractice

import android.util.Log
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
import androidx.compose.runtime.LaunchedEffect
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
    LaunchedEffect(gameId) {
        viewModel.getGameDetails(gameId.toInt())
    }

    val gameDetails = viewModel.gameDetails.value
    val isLoading = viewModel.detailsLoading.value
    val error = viewModel.detailsError.value

    val scrollState = rememberScrollState()

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(gameDetails?.name ?: "Подробности игры", maxLines = 1) },
                navigationIcon = {
                    IconButton(onClick = onBackClick) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Filled.ArrowBack,
                            contentDescription = "Назад"
                        )
                    }
                }
            )
        }
    ) { paddingValues ->
        when {
            isLoading -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    CircularProgressIndicator()
                }
            }

            error != null -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Error: $error")
                }
            }

            gameDetails != null -> {
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
                                .data(gameDetails.background_image)
                                .crossfade(true)
                                .build(),
                            contentDescription = "${gameDetails.name} cover",
                            modifier = Modifier.fillMaxSize(),
                            contentScale = androidx.compose.ui.layout.ContentScale.Crop
                        )

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
                                    text = gameDetails.name,
                                    style = MaterialTheme.typography.headlineMedium,
                                    fontWeight = FontWeight.Bold,
                                    color = Color.White
                                )

                                Text(
                                    text = "${gameDetails.developers?.firstOrNull()?.name ?: "Unknown Developer"} • ${gameDetails.released}",
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
                                    text = "${gameDetails.rating}/5",
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
                            gameDetails.genres!!.take(3).forEach { genre ->
                                SuggestionChip(
                                    onClick = { },
                                    label = { Text(genre.name) }
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
                                gameDetails.platforms!!.forEach { platformWrapper ->
                                    AssistChip(
                                        onClick = { },
                                        label = { Text(platformWrapper.platform.name) },
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

                            gameDetails.description?.let { it1 ->
                                HtmlText(
                                    html = it1,
                                )
                            }
                        }
                    }

                    // todo: поменять на metascore
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
                            text = "${gameDetails.rating}/50",
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
                            text = "${(gameDetails.rating * 8).toInt()}/50",
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
                                name = gameDetails.developers?.firstOrNull()?.name
                                    ?: "Unknown Developer",
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

                            val pcPlatform = gameDetails.platforms?.find { platformWrapper ->
                                platformWrapper.platform.name == "PC"
                            }
                            val pcRequirements = pcPlatform?.requirements

                            Log.e(pcRequirements.toString(), pcRequirements.toString())
                            Log.e(gameId, gameId)
                            Log.e(pcPlatform.toString(), pcPlatform.toString())

                            Column {
                                if (pcPlatform != null && pcRequirements != null) {
                                    if (pcRequirements.minimum?.isNotEmpty() == true) {
                                        Text(
                                            text = "Минимальные:",
                                            fontWeight = FontWeight.Bold,
                                            style = MaterialTheme.typography.bodyMedium
                                        )


                                        Text(
                                            text = pcRequirements.minimum,
                                            style = MaterialTheme.typography.bodySmall
                                        )


                                        Spacer(modifier = Modifier.height(16.dp))
                                    }

                                    if (pcRequirements.recommended?.isNotEmpty() == true) {
                                        Text(
                                            text = "Рекомендованные:",
                                            fontWeight = FontWeight.Bold,
                                            style = MaterialTheme.typography.bodyMedium
                                        )

                                        Text(
                                            text = pcRequirements.recommended,
                                            style = MaterialTheme.typography.bodySmall
                                        )
                                    }
                                } else {
                                    val hasPcPlatform =
                                        gameDetails.platforms?.any { platformWrapper ->
                                            platformWrapper.platform.name == "PC"
                                        } ?: false

                                    if (hasPcPlatform) {
                                        Text("Системные требования недоступны для этой игры")
                                    } else {
                                        Text("Системные требования недоступны для консолей")
                                    }
                                }
                            }
                        }
                    }

                    Spacer(modifier = Modifier.height(80.dp)) // Space for FAB
                }
            }

            else -> {
                Box(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(paddingValues),
                    contentAlignment = Alignment.Center
                ) {
                    Text("Описание недоступно")
                }
            }
        }
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
