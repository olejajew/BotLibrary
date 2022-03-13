package com.doblack.bot_library.constructor.models

data class InstructionsModel(
    val userActions: List<UserActionBlock>?,
    val botScripts: List<BotScriptModel>?,
    val buttons: List<ButtonModel>?
)