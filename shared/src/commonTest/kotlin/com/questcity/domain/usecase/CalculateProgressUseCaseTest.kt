package com.questcity.domain.usecase

import com.questcity.domain.model.Difficulty
import com.questcity.domain.model.Location
import com.questcity.domain.model.Quest
import com.questcity.domain.model.Task
import com.questcity.domain.model.TaskType
import com.questcity.domain.model.UserProgress
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertTrue

class CalculateProgressUseCaseTest {
    private val useCase = CalculateProgressUseCase()

    @Test
    fun `calculate returns zero progress for null`() {
        val quest = Quest(
            id = "1",
            title = "Test",
            description = "Test",
            city = "Test"
        )

        val result = useCase.calculate(null, quest)

        assertEquals(0, result.completedLocations)
        assertEquals(0, result.totalLocations)
        assertEquals(0f, result.percentage)
        assertEquals(0, result.score)
    }

    @Test
    fun `calculate returns correct progress`() {
        val quest = Quest(
            id = "1",
            title = "Test",
            description = "Test",
            city = "Test",
            locations = listOf(
                Location(id = "loc1", questId = "1", lat = 0.0, lon = 0.0),
                Location(id = "loc2", questId = "1", lat = 0.0, lon = 0.0),
                Location(id = "loc3", questId = "1", lat = 0.0, lon = 0.0)
            )
        )

        val progress = UserProgress(
            userId = "user1",
            questId = "1",
            currentLocationIndex = 2,
            completedTasksJson = "[\"0\",\"1\"]",
            score = 25
        )

        val result = useCase.calculate(progress, quest)

        assertEquals(2, result.completedLocations)
        assertEquals(3, result.totalLocations)
        assertEquals(25f / 3f, result.percentage, 0.01f)
        assertEquals(25, result.score)
    }

    @Test
    fun `calculate handles empty locations`() {
        val quest = Quest(
            id = "1",
            title = "Test",
            description = "Test",
            city = "Test"
        )

        val progress = UserProgress(
            userId = "user1",
            questId = "1",
            currentLocationIndex = 0,
            completedTasksJson = "[]",
            score = 0
        )

        val result = useCase.calculate(progress, quest)

        assertEquals(0, result.completedLocations)
        assertEquals(0, result.totalLocations)
        assertEquals(0f, result.percentage)
        assertEquals(0, result.score)
    }
}
