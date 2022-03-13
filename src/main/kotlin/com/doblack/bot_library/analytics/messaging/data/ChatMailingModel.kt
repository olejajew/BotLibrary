package com.doblack.bot_library.analytics.messaging.data

import javax.management.monitor.StringMonitor

data class ChatMailingModel(
    val mailingId: String = "",
    val chatId: Long = 0L,
    val messageId: Int = 0
)