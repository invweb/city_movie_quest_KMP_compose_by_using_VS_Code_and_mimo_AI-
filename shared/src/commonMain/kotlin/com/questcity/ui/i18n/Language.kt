package com.questcity.ui.i18n

import androidx.compose.runtime.staticCompositionLocalOf

val LocalStrings = staticCompositionLocalOf { English }

enum class Language(val code: String, val displayName: String) {
    EN("en", "English"),
    RU("ru", "Русский"),
    DE("de", "Deutsch")
}

fun stringsFor(language: Language): Strings = when (language) {
    Language.EN -> English
    Language.RU -> Russian
    Language.DE -> German
}
