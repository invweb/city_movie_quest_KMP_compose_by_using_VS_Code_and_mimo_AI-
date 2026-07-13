package com.questcity.ui.screens.catalog

import com.questcity.domain.model.Difficulty
import com.questcity.domain.model.Quest
import com.questcity.domain.repository.QuestRepository
import com.questcity.domain.repository.ProgressRepository
import com.questcity.domain.usecase.CalculateProgressUseCase
import com.questcity.domain.usecase.ProgressInfo
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

data class CatalogState(
    val quests: List<QuestWithProgress> = emptyList(),
    val isLoading: Boolean = true,
    val error: String? = null,
    val selectedDifficulty: Difficulty? = null
)

data class QuestWithProgress(
    val quest: Quest,
    val progress: ProgressInfo
)

class CatalogViewModel(
    private val questRepository: QuestRepository,
    private val progressRepository: ProgressRepository,
    private val calculateProgress: CalculateProgressUseCase
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private val _state = MutableStateFlow(CatalogState())
    val state: StateFlow<CatalogState> = _state.asStateFlow()

    init {
        loadQuests()
    }

    private fun loadQuests() {
        scope.launch {
            try {
                val allQuests = questRepository.getQuestListOnce()
                val userId = "default_user"
                val questsProgress = mutableListOf<QuestWithProgress>()
                for (quest in allQuests) {
                    val userProgress = progressRepository.getUserProgressOnce(userId, quest.id)
                    val progressInfo = calculateProgress.calculate(userProgress, quest)
                    questsProgress.add(QuestWithProgress(quest, progressInfo))
                }
                _state.value = CatalogState(
                    quests = questsProgress,
                    isLoading = false,
                    error = null,
                    selectedDifficulty = _state.value.selectedDifficulty
                )
            } catch (e: Exception) {
                _state.value = CatalogState(
                    isLoading = false,
                    error = e.message
                )
            }
        }
    }

    fun onDifficultyFilter(difficulty: Difficulty?) {
        _state.value = _state.value.copy(selectedDifficulty = difficulty)
    }

    fun filteredQuests(): List<QuestWithProgress> {
        val difficulty = _state.value.selectedDifficulty
        return if (difficulty == null) {
            _state.value.quests
        } else {
            _state.value.quests.filter { it.quest.difficulty == difficulty }
        }
    }
}
