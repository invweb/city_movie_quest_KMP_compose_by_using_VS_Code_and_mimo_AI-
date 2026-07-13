package com.questcity.data.repository

import com.questcity.data.json.QuestParser
import com.questcity.domain.model.Location
import com.questcity.domain.model.Quest
import com.questcity.domain.model.Task
import com.questcity.domain.model.UserProgress
import com.questcity.domain.repository.ProgressRepository
import com.questcity.domain.repository.QuestRepository
import kotlinx.coroutines.flow.Flow
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.map
import kotlinx.serialization.builtins.ListSerializer
import kotlinx.serialization.builtins.serializer
import kotlinx.serialization.json.Json
import kotlinx.serialization.json.JsonArray
import kotlinx.serialization.json.JsonPrimitive

class InMemoryQuestRepository : QuestRepository {
    private val quests = MutableStateFlow<List<Quest>>(emptyList())

    override fun getAllQuests(): Flow<List<Quest>> = quests

    override suspend fun getQuestById(id: String): Quest? {
        return quests.value.find { it.id == id }
    }

    override suspend fun getLocationsForQuest(questId: String): List<Location> {
        return quests.value.find { it.id == questId }?.locations ?: emptyList()
    }

    override suspend fun getTasksForLocation(locationId: String): List<Task> {
        for (quest in quests.value) {
            for (location in quest.locations) {
                if (location.id == locationId) {
                    return location.tasks
                }
            }
        }
        return emptyList()
    }

    override suspend fun importQuestFromJson(json: String) {
        val quest = QuestParser.parse(json)
        quests.value = quests.value + quest
    }

    override fun getQuestListOnce(): List<Quest> = quests.value
}

class InMemoryProgressRepository : ProgressRepository {
    private val progress = MutableStateFlow<Map<String, UserProgress>>(emptyMap())
    private val json = Json { ignoreUnknownKeys = true }

    override fun getUserProgress(userId: String, questId: String): Flow<UserProgress?> {
        return progress.map { map -> map["$userId:$questId"] }
    }

    override fun getAllUserProgress(userId: String): Flow<List<UserProgress>> {
        return progress.map { map ->
            map.values.filter { it.userId == userId }
        }
    }

    override suspend fun startQuest(userId: String, questId: String) {
        val now = kotlinx.datetime.Clock.System.now().toString()
        val key = "$userId:$questId"
        progress.value = progress.value + (key to UserProgress(
            userId = userId,
            questId = questId,
            currentLocationIndex = 0,
            completedTasksJson = "[]",
            score = 0,
            startedAt = now,
            finishedAt = null
        ))
    }

    override suspend fun completeTask(userId: String, questId: String, locationIndex: Int, points: Int) {
        val key = "$userId:$questId"
        val current = progress.value[key] ?: return

        val completedTasks = try {
            val array = json.parseToJsonElement(current.completedTasksJson) as? JsonArray
                ?: emptyList()
            array.mapNotNull { (it as? JsonPrimitive)?.content }.toMutableList()
        } catch (e: Exception) {
            mutableListOf()
        }

        if (!completedTasks.contains(locationIndex.toString())) {
            completedTasks.add(locationIndex.toString())
        }

        progress.value = progress.value + (key to current.copy(
            currentLocationIndex = locationIndex + 1,
            completedTasksJson = json.encodeToString(
                ListSerializer(String.serializer()),
                completedTasks
            ),
            score = current.score + points
        ))
    }

    override suspend fun finishQuest(userId: String, questId: String) {
        val key = "$userId:$questId"
        val current = progress.value[key] ?: return
        val now = kotlinx.datetime.Clock.System.now().toString()
        progress.value = progress.value + (key to current.copy(finishedAt = now))
    }

    override fun getUserProgressOnce(userId: String, questId: String): UserProgress? {
        return progress.value["$userId:$questId"]
    }
}
