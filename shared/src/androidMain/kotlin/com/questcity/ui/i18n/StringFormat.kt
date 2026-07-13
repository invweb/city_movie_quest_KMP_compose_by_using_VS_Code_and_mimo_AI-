package com.questcity.ui.i18n

actual fun String.formatSafe(vararg args: Any): String {
    return try {
        this.format(*args)
    } catch (e: Exception) {
        this
    }
}
