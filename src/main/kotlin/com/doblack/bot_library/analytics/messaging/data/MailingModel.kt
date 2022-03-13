package com.doblack.bot_library.analytics.messaging.data

import java.util.*

data class MailingModel(
    val message: String = "",
    var images: String = "",
    val buttons: String = "",
    var mailingId: String = UUID.randomUUID().toString(),
    var sentTime: Long = System.currentTimeMillis(),
)