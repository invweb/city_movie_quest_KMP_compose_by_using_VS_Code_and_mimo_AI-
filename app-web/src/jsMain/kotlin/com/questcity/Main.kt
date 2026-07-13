package com.questcity

import androidx.compose.runtime.*
import androidx.compose.ui.window.CanvasBasedWindow
import com.questcity.data.repository.InMemoryProgressRepository
import com.questcity.data.repository.InMemoryQuestRepository
import com.questcity.domain.usecase.CalculateProgressUseCase
import com.questcity.ui.screens.catalog.CatalogViewModel
import com.questcity.ui.screens.catalog.QuestCatalogScreen
import com.questcity.ui.theme.QuestCityTheme

@OptIn(ExperimentalComposeUiApi::class)
fun main() {
    val questRepository = InMemoryQuestRepository()
    val progressRepository = InMemoryProgressRepository()
    val calculateProgress = CalculateProgressUseCase()

    val catalogViewModel = CatalogViewModel(
        questRepository = questRepository,
        progressRepository = progressRepository,
        calculateProgress = calculateProgress
    )

    CanvasBasedWindow(canvasElementId = "ComposeTarget") {
        QuestCityTheme {
            val state by catalogViewModel.state.collectAsState()
            QuestCatalogScreen(
                state = state,
                onQuestSelected = { /* navigate to quest map */ },
                onDifficultyFilter = { catalogViewModel.onDifficultyFilter(it) }
            )
        }
    }
}
