package com.questcity.domain.repository

import com.questcity.domain.model.Location
import com.questcity.domain.model.Quest
import com.questcity.domain.model.Task
import com.questcity.domain.model.UserProgress
import kotlinx.coroutines.flow.Flow

interface QuestRepository {
    fun getAllQuests(): Flow<List<Quest>>
    suspend fun getQuestById(id: String): Quest?
    suspend fun getLocationsForQuest(questId: String): List<Location>
    suspend fun getTasksForLocation(locationId: String): List<Task>
    suspend fun importQuestFromJson(json: String)
    fun getQuestListOnce(): List<Quest>
}

interface ProgressRepository {
    fun getUserProgress(userId: String, questId: String): Flow<UserProgress?>
    fun getAllUserProgress(userId: String): Flow<List<UserProgress>>
    suspend fun startQuest(userId: String, questId: String)
    suspend fun completeTask(userId: String, questId: String, locationIndex: Int, points: Int)
    suspend fun finishQuest(userId: String, questId: String)
    fun getUserProgressOnce(userId: String, questId: String): UserProgress?
}
