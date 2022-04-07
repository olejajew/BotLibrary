package com.doblack.bot_library.analytics.models

data class UserModel(
    val tgUserId: Long = 0L,
    val createdTime: Long = 0L,
    val alive: Boolean = true,
    val blockedTime: Long? = null,
)