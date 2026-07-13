package com.questcity

import android.os.Bundle
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.isSystemInDarkTheme
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.questcity.data.repository.InMemoryProgressRepository
import com.questcity.data.repository.InMemoryQuestRepository
import com.questcity.domain.usecase.CalculateProgressUseCase
import com.questcity.domain.usecase.CheckAnswerUseCase
import com.questcity.ui.i18n.*
import com.questcity.ui.screens.about.AboutScreen
import com.questcity.ui.screens.catalog.CatalogViewModel
import com.questcity.ui.screens.catalog.QuestCatalogScreen
import com.questcity.ui.screens.menu.MainMenuScreen
import com.questcity.ui.screens.menu.MenuItem
import com.questcity.ui.screens.settings.SettingsScreen
import com.questcity.ui.screens.settings.SettingsState
import com.questcity.ui.screens.settings.ThemeMode
import com.questcity.ui.screens.task.TaskAction
import com.questcity.ui.screens.task.TaskScreen
import com.questcity.ui.screens.task.TaskViewModel
import com.questcity.ui.theme.QuestCityTheme

class MainActivity : ComponentActivity() {
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        val questRepository = InMemoryQuestRepository()
        val progressRepository = InMemoryProgressRepository()
        val calculateProgress = CalculateProgressUseCase()
        val checkAnswerUseCase = CheckAnswerUseCase()

        val questFiles = listOf(
            "quest_blade_petersburg.json",
            "quest_indiana_jones_moscow.json",
            "quest_bond_petersburg.json",
            "quest_matrix_moscow.json",
            "quest_titanic_petersburg.json",
            "quest_john_wick_moscow.json",
            "quest_harry_potter_london.json",
            "quest_inception_cities.json",
            "quest_dark_knight_gotham.json"
        )

        for (fileName in questFiles) {
            try {
                val json = assets.open("files/$fileName").bufferedReader().readText()
                kotlinx.coroutines.runBlocking {
                    questRepository.importQuestFromJson(json)
                }
            } catch (e: Exception) {
                e.printStackTrace()
            }
        }

        val catalogViewModel = CatalogViewModel(
            questRepository = questRepository,
            progressRepository = progressRepository,
            calculateProgress = calculateProgress
        )

        val taskViewModel = TaskViewModel(
            questRepository = questRepository,
            progressRepository = progressRepository,
            checkAnswerUseCase = checkAnswerUseCase
        )

        setContent {
            var currentScreen by remember { mutableStateOf<Screen>(Screen.MainMenu) }
            var settingsState by remember { mutableStateOf(SettingsState()) }
            val strings = stringsFor(settingsState.language)
            val isDarkTheme = when (settingsState.themeMode) {
                ThemeMode.LIGHT -> false
                ThemeMode.DARK -> true
                ThemeMode.SYSTEM -> isSystemInDarkTheme()
            }

            QuestCityTheme(darkTheme = isDarkTheme) {
                CompositionLocalProvider(LocalStrings provides strings) {
                    when (currentScreen) {
                        Screen.MainMenu -> MainMenuScreen(
                            onMenuItemClick = { menuItem ->
                                currentScreen = when (menuItem) {
                                    MenuItem.QUESTS -> Screen.Quests
                                    MenuItem.SETTINGS -> Screen.Settings
                                    MenuItem.ABOUT -> Screen.About
                                }
                            }
                        )

                        Screen.Quests -> {
                            val state by catalogViewModel.state.collectAsState()
                            QuestCatalogScreen(
                                state = state,
                                onQuestSelected = { quest ->
                                    taskViewModel.loadQuestAndStart(quest)
                                    currentScreen = Screen.Task
                                },
                                onDifficultyFilter = { catalogViewModel.onDifficultyFilter(it) },
                                onBack = { currentScreen = Screen.MainMenu }
                            )
                        }

                        Screen.Task -> {
                            val state by taskViewModel.state.collectAsState()
                            TaskScreen(
                                state = state,
                                onAction = { action ->
                                    when (action) {
                                        is TaskAction.NextTask -> {
                                            if (state.error != null && state.task == null) {
                                                currentScreen = Screen.Quests
                                            } else {
                                                taskViewModel.onAction(action)
                                            }
                                        }
                                        else -> taskViewModel.onAction(action)
                                    }
                                },
                                onBack = { currentScreen = Screen.Quests }
                            )
                        }

                        Screen.Settings -> SettingsScreen(
                            state = settingsState,
                            onLanguageChanged = { settingsState = settingsState.copy(language = it) },
                            onThemeChanged = { settingsState = settingsState.copy(themeMode = it) },
                            onFontSizeChanged = { settingsState = settingsState.copy(fontSize = it) },
                            onBack = { currentScreen = Screen.MainMenu }
                        )

                        Screen.About -> AboutScreen(
                            onBack = { currentScreen = Screen.MainMenu }
                        )
                    }
                }
            }
        }
    }
}

sealed class Screen {
    object MainMenu : Screen()
    object Quests : Screen()
    object Task : Screen()
    object Settings : Screen()
    object About : Screen()
}
