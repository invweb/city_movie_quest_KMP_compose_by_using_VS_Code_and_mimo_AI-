package com.questcity.domain.usecase

import com.questcity.domain.model.Task
import com.questcity.domain.model.TaskType
import kotlin.test.Test
import kotlin.test.assertFalse
import kotlin.test.assertTrue

class CheckAnswerUseCaseTest {
    private val useCase = CheckAnswerUseCase()

    @Test
    fun `quiz - correct answer returns true`() {
        val task = Task(
            id = "1",
            locationId = "loc1",
            type = TaskType.QUIZ,
            prompt = "Test",
            answerKey = "Answer1"
        )

        val result = useCase.check(task, "Answer1")

        assertTrue(result.isCorrect)
        assertFalse(result.notAtLocation)
    }

    @Test
    fun `quiz - wrong answer returns false`() {
        val task = Task(
            id = "1",
            locationId = "loc1",
            type = TaskType.QUIZ,
            prompt = "Test",
            answerKey = "Answer1"
        )

        val result = useCase.check(task, "WrongAnswer")

        assertFalse(result.isCorrect)
    }

    @Test
    fun `quiz - case insensitive`() {
        val task = Task(
            id = "1",
            locationId = "loc1",
            type = TaskType.QUIZ,
            prompt = "Test",
            answerKey = "Answer1"
        )

        val result = useCase.check(task, "answer1")

        assertTrue(result.isCorrect)
    }

    @Test
    fun `count - correct number returns true`() {
        val task = Task(
            id = "1",
            locationId = "loc1",
            type = TaskType.COUNT,
            prompt = "Test",
            answerKey = "42"
        )

        val result = useCase.check(task, "42")

        assertTrue(result.isCorrect)
    }

    @Test
    fun `count - wrong number returns false`() {
        val task = Task(
            id = "1",
            locationId = "loc1",
            type = TaskType.COUNT,
            prompt = "Test",
            answerKey = "42"
        )

        val result = useCase.check(task, "43")

        assertFalse(result.isCorrect)
    }

    @Test
    fun `count - non-numeric input returns false`() {
        val task = Task(
            id = "1",
            locationId = "loc1",
            type = TaskType.COUNT,
            prompt = "Test",
            answerKey = "42"
        )

        val result = useCase.check(task, "abc")

        assertFalse(result.isCorrect)
    }

    @Test
    fun `fact_or_fict - correct answer returns true`() {
        val task = Task(
            id = "1",
            locationId = "loc1",
            type = TaskType.FACT_OR_FICT,
            prompt = "Test",
            answerKey = "fact"
        )

        val result = useCase.check(task, "fact")

        assertTrue(result.isCorrect)
    }

    @Test
    fun `not at location returns notAtLocation`() {
        val task = Task(
            id = "1",
            locationId = "loc1",
            type = TaskType.QUIZ,
            prompt = "Test",
            answerKey = "Answer1"
        )

        val result = useCase.check(
            task = task,
            userAnswer = "Answer1",
            userLat = 59.9500,
            userLon = 30.3500,
            targetLat = 59.9398,
            targetLon = 30.3146
        )

        assertTrue(result.notAtLocation)
    }
}
