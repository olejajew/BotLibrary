package com.doblack.bot_library.base

import org.telegram.telegrambots.meta.api.objects.Message
import org.telegram.telegrambots.meta.api.objects.Update
import java.lang.Exception

fun Update.chatId(): Long {
    return message?.chatId ?: callbackQuery.message.chatId
}

fun Message.getCommand(): String {
    return this.text.split(" ")[0].trim().split("@")[0]
}

fun Message.getReferrerIfHas(): String? {
    return try {
        this.text.split(" ")[1].trim()
    } catch (e: Exception) {
        null
    }
}

fun Message.checkIsCommand(): Boolean{
    return this.isCommand || this.entities?.firstOrNull { it.type == "bot_command" } != null
}

fun Exception.checkIsBlocked(): Boolean {
    return this.localizedMessage.contains("bot was blocked")
}

fun Message.getUserName(): String {
    var name = if(chat.userName != null){
        chat.userName
    } else {
        "${chat.firstName} ${chat.lastName}"
    }
    if(name.isEmpty()){
        name = "_noname"
    }
    return name
}