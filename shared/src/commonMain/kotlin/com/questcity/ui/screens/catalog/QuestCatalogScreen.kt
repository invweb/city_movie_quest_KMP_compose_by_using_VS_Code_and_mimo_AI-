package com.questcity.ui.screens.catalog

import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.questcity.domain.model.Difficulty
import com.questcity.domain.model.Quest
import com.questcity.ui.i18n.LocalStrings
import com.questcity.ui.i18n.formatSafe

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuestCatalogScreen(
    state: CatalogState,
    onQuestSelected: (Quest) -> Unit,
    onDifficultyFilter: (Difficulty?) -> Unit,
    onBack: (() -> Unit)? = null
) {
    val strings = LocalStrings.current
    val language = strings.language

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(strings.appName) },
                navigationIcon = {
                    if (onBack != null) {
                        IconButton(onClick = onBack) {
                            Text("\u2190")
                        }
                    }
                }
            )
        }
    ) { padding ->
        val filteredQuests = remember(state.quests, state.selectedDifficulty) {
            val diff = state.selectedDifficulty
            if (diff == null) state.quests
            else state.quests.filter { it.quest.difficulty == diff }
        }

        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            DifficultyFilter(
                selectedDifficulty = state.selectedDifficulty,
                onFilterSelected = onDifficultyFilter
            )

            Spacer(modifier = Modifier.height(16.dp))

            when {
                state.isLoading -> {
                    Box(
                        modifier = Modifier.fillMaxSize(),
                        contentAlignment = Alignment.Center
                    ) {
                        CircularProgressIndicator()
                    }
                }
                state.error != null -> {
                    ErrorState(
                        message = state.error,
                        onRetry = { /* retry */ }
                    )
                }
                filteredQuests.isEmpty() -> {
                    EmptyState()
                }
                else -> {
                    LazyColumn(
                        verticalArrangement = Arrangement.spacedBy(12.dp)
                    ) {
                        items(filteredQuests) { questWithProgress ->
                            QuestCard(
                                quest = questWithProgress.quest,
                                progressPercentage = questWithProgress.progress.percentage,
                                score = questWithProgress.progress.score,
                                language = language,
                                onClick = { onQuestSelected(questWithProgress.quest) }
                            )
                        }
                    }
                }
            }
        }
    }
}

@Composable
fun DifficultyFilter(
    selectedDifficulty: Difficulty?,
    onFilterSelected: (Difficulty?) -> Unit
) {
    val strings = LocalStrings.current

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        FilterChip(
            selected = selectedDifficulty == null,
            onClick = { onFilterSelected(null) },
            label = { Text(strings.filterAll) }
        )
        FilterChip(
            selected = selectedDifficulty == Difficulty.EASY,
            onClick = { onFilterSelected(Difficulty.EASY) },
            label = { Text(strings.filterEasy) }
        )
        FilterChip(
            selected = selectedDifficulty == Difficulty.MEDIUM,
            onClick = { onFilterSelected(Difficulty.MEDIUM) },
            label = { Text(strings.filterMedium) }
        )
        FilterChip(
            selected = selectedDifficulty == Difficulty.HARD,
            onClick = { onFilterSelected(Difficulty.HARD) },
            label = { Text(strings.filterHard) }
        )
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun QuestCard(
    quest: Quest,
    progressPercentage: Float,
    score: Int,
    language: com.questcity.ui.i18n.Language,
    onClick: () -> Unit
) {
    val strings = LocalStrings.current
    val difficultyLabel = when (quest.difficulty) {
        Difficulty.EASY -> strings.filterEasy
        Difficulty.MEDIUM -> strings.filterMedium
        Difficulty.HARD -> strings.filterHard
    }

    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = quest.title.get(language),
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = quest.description.get(language),
                style = MaterialTheme.typography.bodyMedium,
                maxLines = 2
            )
            Spacer(modifier = Modifier.height(8.dp))
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween
            ) {
                Text(
                    text = "${quest.city.get(language)} • $difficultyLabel",
                    style = MaterialTheme.typography.bodySmall
                )
                Text(
                    text = "$score ${strings.points}",
                    style = MaterialTheme.typography.bodySmall
                )
            }
            Spacer(modifier = Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = progressPercentage,
                modifier = Modifier.fillMaxWidth()
            )
            Text(
                text = strings.percentComplete.formatSafe((progressPercentage * 100).toInt()),
                style = MaterialTheme.typography.bodySmall,
                modifier = Modifier.padding(top = 4.dp)
            )
        }
    }
}

@Composable
fun ErrorState(
    message: String,
    onRetry: () -> Unit
) {
    val strings = LocalStrings.current

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = message,
            style = MaterialTheme.typography.bodyLarge
        )
        Spacer(modifier = Modifier.height(16.dp))
        Button(onClick = onRetry) {
            Text(strings.retry)
        }
    }
}

@Composable
fun EmptyState() {
    val strings = LocalStrings.current

    Column(
        modifier = Modifier.fillMaxSize(),
        horizontalAlignment = Alignment.CenterHorizontally,
        verticalArrangement = Arrangement.Center
    ) {
        Text(
            text = strings.noQuestsAvailable,
            style = MaterialTheme.typography.bodyLarge
        )
    }
}
