package com.questcity.domain.usecase

import com.questcity.domain.geo.GeoUtils
import com.questcity.domain.model.Task
import com.questcity.domain.model.TaskType

class CheckAnswerUseCase {
    fun check(task: Task, userAnswer: String, userLat: Double? = null, userLon: Double? = null,
              targetLat: Double? = null, targetLon: Double? = null): CheckResult {
        val isAtLocation = if (targetLat != null && targetLon != null && userLat != null && userLon != null) {
            GeoUtils.isUserInRadius(userLat, userLon, targetLat, targetLon)
        } else {
            true
        }

        if (!isAtLocation) {
            return CheckResult(notAtLocation = true)
        }

        val isCorrect = when (task.type) {
            TaskType.QUIZ -> userAnswer.trim().lowercase() == task.answerKey.trim().lowercase()
            TaskType.COUNT -> {
                val userNum = userAnswer.trim().toIntOrNull()
                val correctNum = task.answerKey.trim().toIntOrNull()
                userNum != null && correctNum != null && userNum == correctNum
            }
            TaskType.FACT_OR_FICT -> userAnswer.trim().lowercase() == task.answerKey.trim().lowercase()
        }

        return CheckResult(isCorrect = isCorrect, notAtLocation = false)
    }
}

data class CheckResult(
    val isCorrect: Boolean = false,
    val notAtLocation: Boolean = false
)
