package com.questcity.ui.screens.progress

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.lazy.LazyColumn
import androidx.compose.foundation.lazy.items
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.CornerRadius
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.unit.dp
import com.questcity.domain.model.Quest
import com.questcity.domain.repository.ProgressRepository
import com.questcity.domain.usecase.CalculateProgressUseCase
import com.questcity.domain.usecase.ProgressInfo
import com.questcity.ui.i18n.Language
import com.questcity.ui.i18n.LocalStrings
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.launch

data class ProgressScreenState(
    val quests: List<QuestProgressItem> = emptyList(),
    val totalScore: Int = 0,
    val isLoading: Boolean = true,
    val error: String? = null
)

data class QuestProgressItem(
    val quest: Quest,
    val progress: ProgressInfo,
    val isCompleted: Boolean
)

class ProgressViewModel(
    private val progressRepository: ProgressRepository,
    private val calculateProgress: CalculateProgressUseCase
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private val _state = mutableStateOf(ProgressScreenState())
    val state: State<ProgressScreenState> = _state

    fun loadProgress(userId: String, quests: List<Quest>) {
        _state.value = _state.value.copy(isLoading = true)
        scope.launch {
            val items = mutableListOf<QuestProgressItem>()
            for (quest in quests) {
                val userProgress = progressRepository.getUserProgressOnce(userId, quest.id)
                val progress = calculateProgress.calculate(userProgress, quest)
                items.add(
                    QuestProgressItem(
                        quest = quest,
                        progress = progress,
                        isCompleted = userProgress?.finishedAt != null
                    )
                )
            }
            _state.value = _state.value.copy(
                quests = items,
                totalScore = items.sumOf { it.progress.score },
                isLoading = false
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProgressScreen(
    state: ProgressScreenState,
    onQuestClick: (Quest) -> Unit
) {
    val strings = LocalStrings.current
    val language = strings.language

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(strings.progress) }
            )
        }
    ) { padding ->
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
                Box(
                    modifier = Modifier.fillMaxSize(),
                    contentAlignment = Alignment.Center
                ) {
                    Text(state.error)
                }
            }
            else -> {
                LazyColumn(
                    modifier = Modifier
                        .fillMaxSize()
                        .padding(padding)
                        .padding(16.dp),
                    verticalArrangement = Arrangement.spacedBy(12.dp)
                ) {
                    item {
                        ScoreCard(totalScore = state.totalScore)
                    }

                    items(state.quests) { item ->
                        ProgressCard(
                            item = item,
                            language = language,
                            onClick = { onQuestClick(item.quest) }
                        )
                    }

                    item {
                        StatisticsCard(state = state)
                    }
                }
            }
        }
    }
}

@Composable
fun ScoreCard(totalScore: Int) {
    val strings = LocalStrings.current

    Card(
        modifier = Modifier.fillMaxWidth(),
        colors = CardDefaults.cardColors(
            containerColor = MaterialTheme.colorScheme.primaryContainer
        )
    ) {
        Column(
            modifier = Modifier.padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = strings.totalScore,
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(8.dp))
            Text(
                text = "$totalScore",
                style = MaterialTheme.typography.headlineLarge,
                color = MaterialTheme.colorScheme.primary
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ProgressCard(
    item: QuestProgressItem,
    language: Language,
    onClick: () -> Unit
) {
    val strings = LocalStrings.current

    Card(
        onClick = onClick,
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.SpaceBetween,
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = item.quest.title.get(language),
                    style = MaterialTheme.typography.titleMedium
                )
                if (item.isCompleted) {
                    AssistChip(
                        onClick = { },
                        label = { Text(strings.completed) }
                    )
                }
            }
            Spacer(modifier = Modifier.height(8.dp))
            LinearProgressIndicator(
                progress = item.progress.percentage,
                modifier = Modifier.fillMaxWidth()
            )
            Spacer(modifier = Modifier.height(4.dp))
            Text(
                text = "${item.progress.completedLocations} / ${item.progress.totalLocations} ${strings.location.lowercase()} • ${item.progress.score} ${strings.points}",
                style = MaterialTheme.typography.bodySmall
            )
        }
    }
}

@Composable
fun StatisticsCard(state: ProgressScreenState) {
    val strings = LocalStrings.current

    Card(
        modifier = Modifier.fillMaxWidth()
    ) {
        Column(
            modifier = Modifier.padding(16.dp)
        ) {
            Text(
                text = strings.statistics,
                style = MaterialTheme.typography.titleMedium
            )
            Spacer(modifier = Modifier.height(16.dp))

            BarChart(
                data = state.quests.map { it.progress.score.toFloat() },
                labels = state.quests.map { it.quest.title.get(strings.language).take(10) },
                modifier = Modifier
                    .fillMaxWidth()
                    .height(150.dp)
            )

            Spacer(modifier = Modifier.height(16.dp))

            val completedCount = state.quests.count { it.isCompleted }
            Text(
                text = strings.questsFinished.format(completedCount, state.quests.size),
                style = MaterialTheme.typography.bodyMedium
            )
        }
    }
}

@Composable
fun BarChart(
    data: List<Float>,
    labels: List<String>,
    modifier: Modifier = Modifier
) {
    val maxValue = data.maxOrNull() ?: 1f
    val barColor = MaterialTheme.colorScheme.primary
    val backgroundColor = MaterialTheme.colorScheme.surfaceVariant

    Canvas(modifier = modifier) {
        val barWidth = size.width / (data.size * 2f)
        val spacing = barWidth

        data.forEachIndexed { index, value ->
            val barHeight = (value / maxValue) * size.height * 0.8f
            val x = index * (barWidth + spacing) + spacing / 2
            val y = size.height - barHeight

            drawRoundRect(
                color = backgroundColor,
                topLeft = Offset(x, 0f),
                size = Size(barWidth, size.height),
                cornerRadius = CornerRadius(4f, 4f)
            )

            drawRoundRect(
                color = barColor,
                topLeft = Offset(x, y),
                size = Size(barWidth, barHeight),
                cornerRadius = CornerRadius(4f, 4f)
            )
        }
    }
}
