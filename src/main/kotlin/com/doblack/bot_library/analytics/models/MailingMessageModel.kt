package com.doblack.bot_library.analytics.models

data class MailingMessageModel(
    val mailing_id: String = "",
    val chat_id: Long = 0L,
    val message_id: Int = 0
)