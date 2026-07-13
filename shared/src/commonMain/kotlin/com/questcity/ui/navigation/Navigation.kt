package com.questcity.ui.navigation

sealed class Screen(val route: String) {
    object Catalog : Screen("catalog")
    object QuestMap : Screen("quest_map/{questId}") {
        fun createRoute(questId: String) = "quest_map/$questId"
    }
    object Task : Screen("task/{taskId}") {
        fun createRoute(taskId: String) = "task/$taskId"
    }
    object Progress : Screen("progress")
}

sealed class NavigationEvent {
    object NavigateToCatalog : NavigationEvent()
    data class NavigateToQuestMap(val questId: String) : NavigationEvent()
    data class NavigateToTask(val taskId: String) : NavigationEvent()
    object NavigateToProgress : NavigationEvent()
    object NavigateBack : NavigationEvent()
}
