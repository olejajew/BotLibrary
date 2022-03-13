package com.doblack.bot_library.constructor.models

data class BotActionData(
    val text: String?,
    val images: List<String>?,
    val buttons: List<List<String>>?,
    val scriptId: String?
)