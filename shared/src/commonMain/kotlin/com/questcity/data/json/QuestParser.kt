package com.questcity.data.json

import com.questcity.domain.model.Difficulty
import com.questcity.domain.model.LocalizedText
import com.questcity.domain.model.Location
import com.questcity.domain.model.Quest
import com.questcity.domain.model.Task
import com.questcity.domain.model.TaskType
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.Json
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.serializer

@Serializable
data class LocalizedTextJson(
    val en: String = "",
    val ru: String = "",
    val de: String = ""
)

@Serializable
data class QuestJson(
    val id: String,
    val title: LocalizedTextJson = LocalizedTextJson(),
    val description: LocalizedTextJson = LocalizedTextJson(),
    val city: LocalizedTextJson = LocalizedTextJson(),
    val difficulty: String = "medium",
    val durationMin: Int = 60,
    val locations: List<LocationJson> = emptyList()
)

@Serializable
data class LocationJson(
    val id: String,
    val lat: Double,
    val lon: Double,
    val radiusM: Double = 150.0,
    val orderIndex: Int = 0,
    val tasks: List<TaskJson> = emptyList()
)

@Serializable
data class TaskJson(
    val id: String,
    val type: String,
    val prompt: LocalizedTextJson = LocalizedTextJson(),
    val answerKey: String,
    val hints: List<String> = emptyList(),
    val rewardPoints: Int = 10,
    val imdbFact: LocalizedTextJson = LocalizedTextJson()
)

object QuestParser {
    private val json = Json { ignoreUnknownKeys = true; isLenient = true }

    fun parse(jsonString: String): Quest {
        val questJson = json.decodeFromString<QuestJson>(jsonString)
        return mapToDomain(questJson)
    }

    private fun LocalizedTextJson.toDomain() = LocalizedText(en = en, ru = ru, de = de)

    private fun mapToDomain(q: QuestJson): Quest {
        val difficulty = when (q.difficulty.lowercase()) {
            "easy" -> Difficulty.EASY
            "hard" -> Difficulty.HARD
            else -> Difficulty.MEDIUM
        }

        val locations = q.locations.map { loc ->
            Location(
                id = loc.id,
                questId = q.id,
                lat = loc.lat,
                lon = loc.lon,
                radiusM = loc.radiusM,
                orderIndex = loc.orderIndex,
                tasks = loc.tasks.map { task ->
                    Task(
                        id = task.id,
                        locationId = loc.id,
                        type = when (task.type.lowercase()) {
                            "count" -> TaskType.COUNT
                            "fact_or_fict" -> TaskType.FACT_OR_FICT
                            else -> TaskType.QUIZ
                        },
                        prompt = task.prompt.toDomain(),
                        answerKey = task.answerKey,
                        hintsJson = Json.encodeToString(
                            ListSerializer(String.serializer()),
                            task.hints
                        ),
                        rewardPoints = task.rewardPoints,
                        imdbFact = task.imdbFact.toDomain()
                    )
                }
            )
        }

        return Quest(
            id = q.id,
            title = q.title.toDomain(),
            description = q.description.toDomain(),
            city = q.city.toDomain(),
            difficulty = difficulty,
            durationMin = q.durationMin,
            isActive = true,
            locations = locations
        )
    }
}
