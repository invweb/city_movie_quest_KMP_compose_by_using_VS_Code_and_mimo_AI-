package com.questcity.domain.model

import com.questcity.ui.i18n.Language
import kotlinx.serialization.Serializable

@Serializable
data class LocalizedText(
    val en: String = "",
    val ru: String = "",
    val de: String = ""
) {
    fun get(language: Language): String = when (language) {
        Language.EN -> en
        Language.RU -> ru
        Language.DE -> de
    }.ifEmpty { en }
}
