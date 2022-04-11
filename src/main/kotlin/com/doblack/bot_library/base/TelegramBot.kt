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

    private val nextMessageCatchers = hashMapOf<Long, NextMessageCatcher>()

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

    fun stopBot() {
        clearWebhook()
    }

    fun catchNextMessage(chatId: Long, nextMessageCatcher: NextMessageCatcher) {
        nextMessageCatchers[chatId] = nextMessageCatcher
    }

    fun stopCatchNextMessage(chatId: Long) {
        nextMessageCatchers.remove(chatId)
    }

    override fun onUpdateReceived(update: Update) {
        if (nextMessageCatchers.containsKey(update.chatId())) {
            nextMessageCatchers[update.chatId()]!!.onMessageReceived(update)
            nextMessageCatchers.remove(update.chatId())
            return
        }
        when {
            update.hasCallbackQuery() -> {
                callbackMessageReceived(update)
            }
            update.message.hasDocument() -> documentReceived(update)
            update.message.hasPhoto() -> photoReceived(update)
            update.message.checkIsCommand() -> commandReceived(update)
            else -> messageReceived(update)
        }
    }

    fun createMedia(file: File): InputMediaPhoto {
        val media = InputMediaPhoto()
        media.setMedia(file, file.nameWithoutExtension)
        return media
    }

    abstract fun documentReceived(update: Update)

    abstract fun photoReceived(update: Update)

    abstract fun commandReceived(update: Update)

    abstract fun messageReceived(update: Update)

    abstract fun callbackMessageReceived(update: Update)

}