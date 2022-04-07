package com.doblack.bot_library.base

import org.telegram.telegrambots.meta.api.objects.Message
import org.telegram.telegrambots.meta.api.objects.Update

interface UserLifecycleObserver {

    fun onStartCommand(update: Update)

    fun onUserBlocked(chatId: Long)

}