package com.doblack.bot_library.constructor.models

data class BotScriptModel(
    val scriptId: String,
    val name: String,
    val actions: List<BotActionModel>
)