package com.doblack.bot_library.analytics.models

import com.doblack.bot_library.base.models.BotButton
import java.util.*

data class MailingModel(
    val message: String = "",
    var images: List<String> = emptyList(),
    val buttons: List<BotButton> = emptyList(),
    var mailingId: String = UUID.randomUUID().toString(),
    var sentTime: Long = System.currentTimeMillis(),
)