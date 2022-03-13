package com.doblack.bot_library.constructor.models

data class ButtonModel(
    val id: String,
    val name: String,
    val text: String,
    val type: Int,
    val botScriptId: String?,
    val link: String?
)