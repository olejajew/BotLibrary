package com.doblack.bot_library.base

import org.telegram.telegrambots.bots.TelegramLongPollingBot
import org.telegram.telegrambots.meta.TelegramBotsApi
import org.telegram.telegrambots.meta.api.objects.Message
import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.meta.api.objects.media.InputMediaPhoto
import org.telegram.telegrambots.meta.exceptions.TelegramApiException
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession
import java.io.File

abstract class TelegramBot : TelegramLongPollingBot() {

    open fun runBot(onStart: (success: Boolean) -> Unit) {
        val botsApi = TelegramBotsApi(DefaultBotSession::class.java)
        try {
            botsApi.registerBot(this)
            onStart(true)
        } catch (e: TelegramApiException) {
            e.printStackTrace()
            onStart(false)
        }
    }

    override fun onUpdateReceived(update: Update) {
        when {
            update.hasCallbackQuery() -> {
                callbackMessageReceived(update)
            }
            update.message.hasDocument() -> documentReceived(update.message)
            update.message.hasPhoto() -> photoReceived(update.message)
            update.message.checkIsCommand() -> commandReceived(update.message)
            else -> messageReceived(update.message)
        }
    }

    fun createMedia(file: File): InputMediaPhoto {
        val media = InputMediaPhoto()
        media.setMedia(file, file.nameWithoutExtension)
        return media
    }

    abstract fun documentReceived(message: Message)

    abstract fun photoReceived(message: Message)

    abstract fun commandReceived(message: Message)

    abstract fun messageReceived(message: Message)

    abstract fun callbackMessageReceived(update: Update)

}