package com.doblack.bot_library.base

import org.telegram.telegrambots.meta.api.objects.Update

interface NextMessageCatcher {

    fun onMessageReceived(update: Update)

}