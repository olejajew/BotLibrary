package com.doblack.bot_library.analytics.messaging.data

data class PostSendMessageModel(
    val botId: String,
    val message: String,
    val imagesInBase64: List<String>?,
    val buttons: List<UrlButtonModel>?,
    val sentTime: Long? = null,
    val id: String? = null
)
