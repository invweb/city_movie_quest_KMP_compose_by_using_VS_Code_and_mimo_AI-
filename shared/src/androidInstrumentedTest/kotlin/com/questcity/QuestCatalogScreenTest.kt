package com.questcity

import androidx.compose.ui.test.assertIsDisplayed
import androidx.compose.ui.test.junit4.createComposeRule
import androidx.compose.ui.test.onNodeWithText
import androidx.compose.ui.test.performClick
import com.questcity.domain.model.Difficulty
import com.questcity.domain.model.Quest
import com.questcity.ui.screens.catalog.QuestCard
import com.questcity.ui.screens.catalog.QuestWithProgress
import com.questcity.domain.usecase.ProgressInfo
import org.junit.Rule
import org.junit.Test

class QuestCatalogScreenTest {
    @get:Rule
    val composeTestRule = createComposeRule()

    @Test
    fun questCardDisplaysTitle() {
        val quest = Quest(
            id = "test",
            title = "Test Quest",
            description = "Test Description",
            city = "Test City",
            difficulty = Difficulty.EASY
        )
        val progress = ProgressInfo(0, 5, 0f, 0)

        composeTestRule.setContent {
            QuestCard(
                quest = quest,
                progressPercentage = progress.percentage,
                score = progress.score,
                onClick = { }
            )
        }

        composeTestRule.onNodeWithText("Test Quest").assertIsDisplayed()
    }

    @Test
    fun questCardClickable() {
        var clicked = false
        val quest = Quest(
            id = "test",
            title = "Clickable Quest",
            description = "Click me",
            city = "City",
            difficulty = Difficulty.MEDIUM
        )
        val progress = ProgressInfo(2, 5, 0.4f, 20)

        composeTestRule.setContent {
            QuestCard(
                quest = quest,
                progressPercentage = progress.percentage,
                score = progress.score,
                onClick = { clicked = true }
            )
        }

        composeTestRule.onNodeWithText("Clickable Quest").performClick()
        assert(clicked)
    }

    @Test
    fun questCardShowsProgress() {
        val quest = Quest(
            id = "test",
            title = "Progress Quest",
            description = "Show progress",
            city = "City",
            difficulty = Difficulty.HARD
        )
        val progress = ProgressInfo(3, 6, 0.5f, 30)

        composeTestRule.setContent {
            QuestCard(
                quest = quest,
                progressPercentage = progress.percentage,
                score = progress.score,
                onClick = { }
            )
        }

        composeTestRule.onNodeWithText("Progress Quest").assertIsDisplayed()
    }
}
