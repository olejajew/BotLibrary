package com.doblack.bot_library.analytics.utils

object ListUtils {

    const val LIST_ITEMS_SEPARATOR = "&%_"

    fun convertListToString(list: List<String>): String {
        return list.joinToString(LIST_ITEMS_SEPARATOR)
    }

    fun convertStringToList(string: String): List<String> {
        return string.split(LIST_ITEMS_SEPARATOR)
            .filter { it.isNotEmpty() }
    }

}