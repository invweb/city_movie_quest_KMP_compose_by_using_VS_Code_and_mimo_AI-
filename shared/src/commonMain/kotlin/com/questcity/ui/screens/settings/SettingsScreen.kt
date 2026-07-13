package com.questcity.ui.screens.settings

import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import com.questcity.ui.i18n.Language
import com.questcity.ui.i18n.LocalStrings

enum class ThemeMode {
    LIGHT, DARK, SYSTEM
}

enum class FontSize {
    SMALL, NORMAL, LARGE
}

data class SettingsState(
    val language: Language = Language.EN,
    val themeMode: ThemeMode = ThemeMode.SYSTEM,
    val fontSize: FontSize = FontSize.NORMAL
)

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun SettingsScreen(
    state: SettingsState,
    onLanguageChanged: (Language) -> Unit,
    onThemeChanged: (ThemeMode) -> Unit,
    onFontSizeChanged: (FontSize) -> Unit,
    onBack: () -> Unit
) {
    val strings = LocalStrings.current

    Scaffold(
        topBar = {
            TopAppBar(
                title = { Text(strings.settings) },
                navigationIcon = {
                    IconButton(onClick = onBack) {
                        Text("\u2190")
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .padding(16.dp)
        ) {
            SettingsSection(title = strings.languageLabel) {
                LanguageSelector(
                    selectedLanguage = state.language,
                    onLanguageSelected = onLanguageChanged
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            SettingsSection(title = strings.theme) {
                ThemeSelector(
                    selectedTheme = state.themeMode,
                    onThemeSelected = onThemeChanged
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            SettingsSection(title = strings.fontSize) {
                FontSizeSelector(
                    selectedSize = state.fontSize,
                    onSizeSelected = onFontSizeChanged
                )
            }
        }
    }
}

@Composable
fun SettingsSection(
    title: String,
    content: @Composable ColumnScope.() -> Unit
) {
    Column {
        Text(
            text = title,
            style = MaterialTheme.typography.titleMedium,
            modifier = Modifier.padding(bottom = 8.dp)
        )
        content()
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun LanguageSelector(
    selectedLanguage: Language,
    onLanguageSelected: (Language) -> Unit
) {
    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        Language.entries.forEach { language ->
            FilterChip(
                selected = language == selectedLanguage,
                onClick = { onLanguageSelected(language) },
                label = { Text(language.displayName) },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun ThemeSelector(
    selectedTheme: ThemeMode,
    onThemeSelected: (ThemeMode) -> Unit
) {
    val strings = LocalStrings.current
    val themes = listOf(
        ThemeMode.LIGHT to strings.themeLight,
        ThemeMode.DARK to strings.themeDark,
        ThemeMode.SYSTEM to strings.themeSystem
    )

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        themes.forEach { (theme, label) ->
            FilterChip(
                selected = theme == selectedTheme,
                onClick = { onThemeSelected(theme) },
                label = { Text(label) },
                modifier = Modifier.weight(1f)
            )
        }
    }
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun FontSizeSelector(
    selectedSize: FontSize,
    onSizeSelected: (FontSize) -> Unit
) {
    val strings = LocalStrings.current
    val sizes = listOf(
        FontSize.SMALL to strings.fontSizeSmall,
        FontSize.NORMAL to strings.fontSizeNormal,
        FontSize.LARGE to strings.fontSizeLarge
    )

    Row(
        modifier = Modifier.fillMaxWidth(),
        horizontalArrangement = Arrangement.spacedBy(8.dp)
    ) {
        sizes.forEach { (size, label) ->
            FilterChip(
                selected = size == selectedSize,
                onClick = { onSizeSelected(size) },
                label = { Text(label) },
                modifier = Modifier.weight(1f)
            )
        }
    }
}
