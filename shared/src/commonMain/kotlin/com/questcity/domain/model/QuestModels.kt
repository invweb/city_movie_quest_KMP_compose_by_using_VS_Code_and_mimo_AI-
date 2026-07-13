package com.questcity.domain.model

import kotlinx.serialization.Serializable

@Serializable
data class Quest(
    val id: String,
    val title: LocalizedText = LocalizedText(),
    val description: LocalizedText = LocalizedText(),
    val city: LocalizedText = LocalizedText(),
    val difficulty: Difficulty = Difficulty.MEDIUM,
    val durationMin: Int = 60,
    val isActive: Boolean = true,
    val locations: List<Location> = emptyList()
)

enum class Difficulty(val label: String) {
    EASY("Easy"),
    MEDIUM("Medium"),
    HARD("Hard")
}

@Serializable
data class Location(
    val id: String,
    val questId: String,
    val lat: Double,
    val lon: Double,
    val radiusM: Double = 150.0,
    val orderIndex: Int = 0,
    val tasks: List<Task> = emptyList()
)

@Serializable
data class Task(
    val id: String,
    val locationId: String,
    val type: TaskType,
    val prompt: LocalizedText = LocalizedText(),
    val answerKey: String,
    val hintsJson: String = "[]",
    val rewardPoints: Int = 10,
    val imdbFact: LocalizedText = LocalizedText()
)

enum class TaskType(val label: String) {
    QUIZ("Quiz"),
    COUNT("Count"),
    FACT_OR_FICT("Fact or Fiction")
}

@Serializable
data class UserProgress(
    val userId: String,
    val questId: String,
    val currentLocationIndex: Int = 0,
    val completedTasksJson: String = "[]",
    val score: Int = 0,
    val startedAt: String = "",
    val finishedAt: String? = null
)
