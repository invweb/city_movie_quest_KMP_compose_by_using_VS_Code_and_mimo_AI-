package com.questcity.ui.i18n

actual fun String.formatSafe(vararg args: Any): String {
    var result = this
    args.forEachIndexed { index, arg ->
        result = result.replace("%${index + 1}\$s", arg.toString())
            .replace("%${index + 1}\$d", arg.toString())
            .replace("%s", arg.toString())
            .replace("%d", arg.toString())
    }
    return result
}
