package com.doblack.bot_library.analytics.messaging.data

data class UpdateMessageModel(
    val botId: String,
    val message: String,
    val newImagesInBase64: List<String>?,
    val buttons: List<UrlButtonModel>?,
    val id: String,
    val sentTime: Long = System.currentTimeMillis()
)
