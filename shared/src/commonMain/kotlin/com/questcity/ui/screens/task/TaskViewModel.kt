package com.questcity.ui.screens.task

import com.questcity.domain.model.Quest
import com.questcity.domain.model.Task
import com.questcity.domain.repository.ProgressRepository
import com.questcity.domain.repository.QuestRepository
import com.questcity.domain.usecase.CheckAnswerUseCase
import com.questcity.domain.usecase.CheckResult
import kotlinx.coroutines.CoroutineScope
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.SupervisorJob
import kotlinx.coroutines.delay
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.asStateFlow
import kotlinx.coroutines.launch

const val TASK_TIME_LIMIT_SECONDS = 120
const val MAX_HINTS = 2
const val MAX_ATTEMPTS = 3
const val WRONG_ANSWER_PENALTY = -5

data class TaskState(
    val task: Task? = null,
    val userAnswer: String = "",
    val isLoading: Boolean = true,
    val timeRemaining: Int = TASK_TIME_LIMIT_SECONDS,
    val showHints: Boolean = false,
    val hintsShown: Int = 0,
    val result: CheckResult? = null,
    val isAnswerSubmitted: Boolean = false,
    val attempts: Int = 0,
    val isCompleted: Boolean = false,
    val currentQuest: Quest? = null,
    val currentLocationIndex: Int = 0,
    val totalScore: Int = 0,
    val userLat: Double? = null,
    val userLon: Double? = null,
    val targetLat: Double? = null,
    val targetLon: Double? = null,
    val error: String? = null
)

sealed class TaskAction {
    data class AnswerChanged(val answer: String) : TaskAction()
    object SubmitAnswer : TaskAction()
    object ShowHint : TaskAction()
    object RetryAnswer : TaskAction()
    object NextTask : TaskAction()
    data class UpdateLocation(val lat: Double, val lon: Double) : TaskAction()
}

class TaskViewModel(
    private val questRepository: QuestRepository,
    private val progressRepository: ProgressRepository,
    private val checkAnswerUseCase: CheckAnswerUseCase
) {
    private val scope = CoroutineScope(SupervisorJob() + Dispatchers.Default)
    private val _state = MutableStateFlow(TaskState())
    val state: StateFlow<TaskState> = _state.asStateFlow()
    private val userId = "default_user"

    fun loadQuestAndStart(quest: Quest) {
        scope.launch {
            progressRepository.startQuest(userId, quest.id)
            val firstLocation = quest.locations.sortedBy { it.orderIndex }.firstOrNull()
            val firstTask = firstLocation?.tasks?.firstOrNull()
            if (firstTask != null) {
                _state.value = TaskState(
                    task = firstTask,
                    isLoading = false,
                    timeRemaining = TASK_TIME_LIMIT_SECONDS,
                    currentQuest = quest,
                    currentLocationIndex = 0,
                    targetLat = firstLocation.lat,
                    targetLon = firstLocation.lon
                )
                startTimer()
            } else {
                _state.value = TaskState(isLoading = false, error = "No tasks found")
            }
        }
    }

    fun onAction(action: TaskAction) {
        when (action) {
            is TaskAction.AnswerChanged -> {
                if (!_state.value.isAnswerSubmitted) {
                    _state.value = _state.value.copy(userAnswer = action.answer)
                }
            }
            is TaskAction.SubmitAnswer -> submitAnswer()
            is TaskAction.ShowHint -> showHint()
            is TaskAction.RetryAnswer -> retryAnswer()
            is TaskAction.NextTask -> loadNextTask()
            is TaskAction.UpdateLocation -> {
                _state.value = _state.value.copy(userLat = action.lat, userLon = action.lon)
            }
        }
    }

    private fun submitAnswer() {
        val state = _state.value
        val task = state.task ?: return
        if (state.userAnswer.isBlank()) return

        val newAttempts = state.attempts + 1
        val result = checkAnswerUseCase.check(
            task = task,
            userAnswer = state.userAnswer,
            userLat = state.userLat,
            userLon = state.userLon,
            targetLat = state.targetLat,
            targetLon = state.targetLon
        )

        if (result.isCorrect) {
            val score = task.rewardPoints - (newAttempts - 1) * kotlin.math.abs(WRONG_ANSWER_PENALTY)
            _state.value = state.copy(
                result = result,
                isAnswerSubmitted = true,
                isCompleted = true,
                attempts = newAttempts,
                totalScore = state.totalScore + score
            )
            saveProgress(score)
        } else {
            _state.value = state.copy(
                result = result,
                isAnswerSubmitted = true,
                attempts = newAttempts
            )
        }
    }

    private fun retryAnswer() {
        val state = _state.value
        if (state.attempts < MAX_ATTEMPTS) {
            _state.value = state.copy(
                userAnswer = "",
                result = null,
                isAnswerSubmitted = false
            )
        }
    }

    private fun saveProgress(points: Int) {
        val state = _state.value
        val quest = state.currentQuest ?: return
        scope.launch {
            progressRepository.completeTask(
                userId = userId,
                questId = quest.id,
                locationIndex = state.currentLocationIndex,
                points = points
            )
        }
    }

    private fun loadNextTask() {
        val state = _state.value
        val quest = state.currentQuest ?: return
        val locations = quest.locations.sortedBy { it.orderIndex }
        val nextLocationIndex = state.currentLocationIndex + 1

        if (nextLocationIndex < locations.size) {
            val nextLocation = locations[nextLocationIndex]
            val nextTask = nextLocation.tasks.firstOrNull()
            if (nextTask != null) {
                _state.value = TaskState(
                    task = nextTask,
                    isLoading = false,
                    timeRemaining = TASK_TIME_LIMIT_SECONDS,
                    currentQuest = quest,
                    currentLocationIndex = nextLocationIndex,
                    totalScore = state.totalScore,
                    targetLat = nextLocation.lat,
                    targetLon = nextLocation.lon
                )
                startTimer()
                return
            }
        }
        _state.value = state.copy(isCompleted = true, task = null, error = "Quest completed! Score: ${state.totalScore}")
    }

    private fun showHint() {
        val state = _state.value
        if (state.hintsShown < MAX_HINTS) {
            _state.value = state.copy(showHints = true, hintsShown = state.hintsShown + 1)
        }
    }

    private fun startTimer() {
        scope.launch {
            while (_state.value.timeRemaining > 0 && !_state.value.isCompleted) {
                delay(1000)
                _state.value = _state.value.copy(timeRemaining = _state.value.timeRemaining - 1)
            }
        }
    }
}
