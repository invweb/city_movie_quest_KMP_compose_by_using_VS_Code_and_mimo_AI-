package com.questcity.data

import com.questcity.data.json.QuestParser
import kotlin.test.Test
import kotlin.test.assertEquals
import kotlin.test.assertNotNull
import kotlin.test.assertTrue

class QuestParserTest {
    @Test
    fun `parse quest from JSON`() {
        val json = """
        {
          "id": "test_quest",
          "title": "Test Quest",
          "description": "A test quest",
          "city": "Test City",
          "difficulty": "easy",
          "durationMin": 30,
          "locations": [
            {
              "id": "loc1",
              "lat": 59.9398,
              "lon": 30.3146,
              "radiusM": 150.0,
              "orderIndex": 0,
              "tasks": [
                {
                  "id": "task1",
                  "type": "quiz",
                  "prompt": "What is 2+2?",
                  "answerKey": "4",
                  "hints": ["Math"],
                  "rewardPoints": 10,
                  "imdbFact": "Test fact"
                }
              ]
            }
          ]
        }
        """.trimIndent()

        val quest = QuestParser.parse(json)

        assertEquals("test_quest", quest.id)
        assertEquals("Test Quest", quest.title)
        assertEquals("Test City", quest.city)
        assertEquals(1, quest.locations.size)
        assertEquals(1, quest.locations[0].tasks.size)
        assertEquals("4", quest.locations[0].tasks[0].answerKey)
    }

    @Test
    fun `parse quest with multiple locations`() {
        val json = """
        {
          "id": "multi_quest",
          "title": "Multi Quest",
          "description": "Multiple locations",
          "city": "City",
          "locations": [
            {
              "id": "loc1",
              "lat": 59.9398,
              "lon": 30.3146,
              "orderIndex": 0,
              "tasks": []
            },
            {
              "id": "loc2",
              "lat": 59.9400,
              "lon": 30.3160,
              "orderIndex": 1,
              "tasks": []
            }
          ]
        }
        """.trimIndent()

        val quest = QuestParser.parse(json)

        assertEquals(2, quest.locations.size)
    }

    @Test
    fun `parse quest with different task types`() {
        val json = """
        {
          "id": "types_quest",
          "title": "Types Quest",
          "description": "Different types",
          "city": "City",
          "locations": [
            {
              "id": "loc1",
              "lat": 59.9398,
              "lon": 30.3146,
              "tasks": [
                {
                  "id": "quiz_task",
                  "type": "quiz",
                  "prompt": "Quiz",
                  "answerKey": "A"
                },
                {
                  "id": "count_task",
                  "type": "count",
                  "prompt": "Count",
                  "answerKey": "5"
                },
                {
                  "id": "fact_task",
                  "type": "fact_or_fict",
                  "prompt": "Fact",
                  "answerKey": "fact"
                }
              ]
            }
          ]
        }
        """.trimIndent()

        val quest = QuestParser.parse(json)

        assertEquals(3, quest.locations[0].tasks.size)
        assertEquals(com.questcity.domain.model.TaskType.QUIZ, quest.locations[0].tasks[0].type)
        assertEquals(com.questcity.domain.model.TaskType.COUNT, quest.locations[0].tasks[1].type)
        assertEquals(com.questcity.domain.model.TaskType.FACT_OR_FICT, quest.locations[0].tasks[2].type)
    }
}
