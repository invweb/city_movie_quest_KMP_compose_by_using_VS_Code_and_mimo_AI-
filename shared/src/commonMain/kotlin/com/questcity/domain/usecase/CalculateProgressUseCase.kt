package com.questcity.domain.usecase

import com.questcity.domain.model.Quest
import com.questcity.domain.model.UserProgress
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.jsonPrimitive

class CalculateProgressUseCase {
    private val json = Json { ignoreUnknownKeys = true }

    fun calculate(progress: UserProgress?, quest: Quest): ProgressInfo {
        if (progress == null || quest.locations.isEmpty()) {
            return ProgressInfo(0, 0, 0f, 0)
        }

        val completedTasks = parseCompletedTasks(progress.completedTasksJson)
        val totalTasks = quest.locations.size
        val completedCount = completedTasks.size
        val percentage = if (totalTasks > 0) completedCount.toFloat() / totalTasks else 0f

        return ProgressInfo(
            completedLocations = completedCount,
            totalLocations = totalTasks,
            percentage = percentage,
            score = progress.score
        )
    }

    private fun parseCompletedTasks(jsonString: String): List<String> {
        return try {
            val array = json.parseToJsonElement(jsonString) as? JsonArray ?: return emptyList()
            array.mapNotNull { it.jsonPrimitive.content }
        } catch (e: Exception) {
            emptyList()
        }
    }
}

data class ProgressInfo(
    val completedLocations: Int,
    val totalLocations: Int,
    val percentage: Float,
    val score: Int
)
