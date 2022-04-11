package com.doblack.bot_library.base

import com.doblack.bot_library.base.models.BotButton
import com.doblack.bot_library.base.models.ImageInputStream
import org.telegram.telegrambots.meta.api.methods.ActionType
import org.telegram.telegrambots.meta.api.methods.send.SendChatAction
import org.telegram.telegrambots.meta.api.methods.send.SendMediaGroup
import org.telegram.telegrambots.meta.api.methods.send.SendMessage
import org.telegram.telegrambots.meta.api.methods.send.SendPhoto
import org.telegram.telegrambots.meta.api.methods.updatingmessages.*
import org.telegram.telegrambots.meta.api.objects.InputFile
import org.telegram.telegrambots.meta.api.objects.Message
import org.telegram.telegrambots.meta.api.objects.Update
import org.telegram.telegrambots.meta.api.objects.media.InputMediaPhoto
import org.telegram.telegrambots.meta.api.objects.replykeyboard.InlineKeyboardMarkup
import org.telegram.telegrambots.meta.exceptions.TelegramApiException
import java.io.File
import java.lang.Exception

abstract class ChatBot : TelegramBot() {

    private val keyboardButtons = arrayListOf<BotButton>()
    private var userLifecycleObservers: ArrayList<UserLifecycleObserver> = arrayListOf()

    override fun commandReceived(update: Update) {
        if (update.message.getCommand() == "/start") {
            userLifecycleObservers.forEach {
                it.onStartCommand(update)
            }
        }
        onCommandReceived(update)
    }

    abstract fun onCommandReceived(update: Update)

    fun editMessage(
        chatId: Long,
        messageId: Int,
        message: String,
        botButtons: List<List<BotButton>>? = null
    ) {
        val editMessage = EditMessageText()
        editMessage.chatId = chatId.toString()
        editMessage.messageId = messageId
        editMessage.text = message
        if (botButtons != null) {
            editMessage.replyMarkup = ChatBotHelper.prepareInlineButtons(botButtons) as InlineKeyboardMarkup
        }
        try {
            execute(editMessage)
        } catch (e: TelegramApiException) {
            e.printStackTrace()
            if (e.checkIsBlocked()) {
                userBlocked(chatId)
            }
        }
    }

    fun editMessageWithImage(
        chatId: Long,
        messageId: Int,
        message: String,
        file: File,
        botButtons: List<List<BotButton>>? = null
    ) {
        val editMessage = EditMessageMedia()
        editMessage.chatId = chatId.toString()
        editMessage.messageId = messageId
        val inputMedia = InputMediaPhoto()
        inputMedia.setMedia(file, file.nameWithoutExtension)
        editMessage.media = inputMedia
        inputMedia.caption = message
        if (botButtons != null) {
            editMessage.replyMarkup = ChatBotHelper.prepareInlineButtons(botButtons) as InlineKeyboardMarkup
        }

        try {
            execute(editMessage)
        } catch (e: TelegramApiException) {
            if (e.checkIsBlocked()) {
                userBlocked(chatId)
            }
        }
    }

    fun editMessageWithImageInputStream(
        chatId: Long,
        messageId: Int,
        message: String,
        imageInputStream: ImageInputStream,
        botButtons: List<List<BotButton>>? = null
    ) {
        val editMessage = EditMessageMedia()
        editMessage.chatId = chatId.toString()
        editMessage.messageId = messageId
        val inputMedia = InputMediaPhoto()
        inputMedia.setMedia(imageInputStream.inputStream, imageInputStream.fileName)
        editMessage.media = inputMedia
        inputMedia.caption = message
        if (botButtons != null) {
            editMessage.replyMarkup = ChatBotHelper.prepareInlineButtons(botButtons) as InlineKeyboardMarkup
        }

        try {
            execute(editMessage)
        } catch (e: TelegramApiException) {
            if (e.checkIsBlocked()) {
                userBlocked(chatId)
            }
        }
    }

    fun editMessageCaption(
        chatId: Long,
        messageId: Int,
        message: String,
        buttonsList: List<List<BotButton>>?
    ) {
        val editMessageCaption = EditMessageCaption()
        editMessageCaption.chatId = chatId.toString()
        editMessageCaption.messageId = messageId
        editMessageCaption.caption = message
        if (buttonsList != null) {
            editMessageCaption.replyMarkup = ChatBotHelper.prepareInlineButtons(buttonsList) as InlineKeyboardMarkup
        }
        try {
            execute(editMessageCaption)
        } catch (e: TelegramApiException) {
            if (e.checkIsBlocked()) {
                userBlocked(chatId)
            }
        }
    }

    fun deleteMessage(chatId: Long, messageId: Int) {
        val deleteMessage = DeleteMessage()
        deleteMessage.chatId = chatId.toString()
        deleteMessage.messageId = messageId
        try {
            execute(deleteMessage)
        } catch (e: TelegramApiException) {
            if (e.checkIsBlocked()) {
                userBlocked(chatId)
            }
        }
    }

    fun sendMessageWithImage(
        file: File,
        chatId: Long,
        message: String? = null,
        buttons: List<List<BotButton>>? = null,
        isInline: Boolean = true
    ): Message? {
        val sendPhotoRequest = SendPhoto()
        sendPhotoRequest.chatId = chatId.toString()
        sendPhotoRequest.photo = InputFile(file)
        if (message != null) {
            sendPhotoRequest.caption = message
        }
        if (buttons != null) {
            if (isInline) {
                sendPhotoRequest.replyMarkup = ChatBotHelper.prepareInlineButtons(buttons)
            } else {
                sendPhotoRequest.replyMarkup = ChatBotHelper.prepareReplyButtons(buttons, keyboardButtons)
            }
        }
        return try {
            execute(sendPhotoRequest)
        } catch (e: TelegramApiException) {
            if (e.checkIsBlocked()) {
                userBlocked(chatId)
            }
            e.printStackTrace()
            null
        }
    }

    fun sendMessageWithImageInputStream(
        file: ImageInputStream,
        chatId: Long,
        message: String? = null,
        buttons: List<List<BotButton>>? = null,
        isInline: Boolean = true
    ): Message? {
        val sendPhotoRequest = SendPhoto()
        sendPhotoRequest.chatId = chatId.toString()
        sendPhotoRequest.photo = InputFile(file.inputStream, file.fileName)
        if (message != null) {
            sendPhotoRequest.caption = message
        }
        if (buttons != null) {
            if (isInline) {
                sendPhotoRequest.replyMarkup = ChatBotHelper.prepareInlineButtons(buttons)
            } else {
                sendPhotoRequest.replyMarkup = ChatBotHelper.prepareReplyButtons(buttons, keyboardButtons)
            }
        }
        return try {
            execute(sendPhotoRequest)
        } catch (e: TelegramApiException) {
            if (e.checkIsBlocked()) {
                userBlocked(chatId)
            }
            e.printStackTrace()
            null
        }
    }

    fun sendImagesGroupInputStream(
        files: List<ImageInputStream>,
        chatId: Long,
        message: String? = null
    ): List<Message>? {
        val mediaGroup = SendMediaGroup()
        mediaGroup.allowSendingWithoutReply = true
        val medias = files.map {
            val inputMedia = InputMediaPhoto()
            inputMedia.setMedia(it.inputStream, it.fileName)
            inputMedia.parseMode
            if (message != null) {
                inputMedia.caption = message
            }
            inputMedia
        }
        mediaGroup.medias = medias
        mediaGroup.chatId = chatId.toString()

        return try {
            execute(mediaGroup)
        } catch (e: TelegramApiException) {
            if (e.checkIsBlocked()) {
                userBlocked(chatId)
            }
            e.printStackTrace()
            null
        }
    }

    fun sendImagesGroup(files: List<File>, chatId: Long, message: String? = null): List<Message>? {
        val mediaGroup = SendMediaGroup()
        mediaGroup.allowSendingWithoutReply = true
        val medias = files.sorted().map {
            val inputMedia = InputMediaPhoto()
            inputMedia.setMedia(it, it.nameWithoutExtension)
            inputMedia.parseMode
            if (message != null) {
                inputMedia.caption = message
            }
            inputMedia
        }
        mediaGroup.medias = medias
        mediaGroup.chatId = chatId.toString()

        return try {
            execute(mediaGroup)
        } catch (e: TelegramApiException) {
            if (e.checkIsBlocked()) {
                userBlocked(chatId)
            }
            e.printStackTrace()
            null
        }
    }

    open fun sendMessage(text: String, chatId: Long, markDown: Boolean = false): Message? {
        val sendMessage = SendMessage()
        sendMessage.text = text
        sendMessage.disableWebPagePreview()
        sendMessage.chatId = chatId.toString()
        sendMessage.enableMarkdown(markDown)
        return try {
            execute(sendMessage)
        } catch (e: Exception) {
            if (e.checkIsBlocked()) {
                userBlocked(chatId)
                null
            } else {
                sendMessage.enableMarkdown(false)
                execute(sendMessage)
            }
        }
    }

    open fun sendMessage(
        text: String,
        chatId: Long,
        botButtons: List<List<BotButton>>,
        isInline: Boolean = false,
        markDown: Boolean = false
    ): Message? {
        val sendMessage = SendMessage()
        sendMessage.text = text
        sendMessage.chatId = chatId.toString()
        sendMessage.enableMarkdown(markDown)

        if (isInline) {
            sendMessage.replyMarkup = ChatBotHelper.prepareInlineButtons(botButtons)
        } else {
            sendMessage.replyMarkup = ChatBotHelper.prepareReplyButtons(botButtons, keyboardButtons)
        }

        return try {
            execute(sendMessage)
        } catch (e: Exception) {
            e.printStackTrace()
            if (e.checkIsBlocked()) {
                userBlocked(chatId)
                null
            } else {
                sendMessage.enableMarkdown(false)
                execute(sendMessage)
            }
        }
    }

    fun tryChatAction(chatId: Long) {
        val chatAction = SendChatAction()
        chatAction.setAction(ActionType.TYPING)
        chatAction.chatId = chatId.toString()
        try {
            execute(chatAction)
        } catch (e: Exception) {
            if (e.checkIsBlocked()) {
                userBlocked(chatId)
            }
        }
    }

    open fun userBlocked(chatId: Long) {
        userLifecycleObservers.forEach {
            it.onUserBlocked(chatId)
        }
    }

    fun addUserLifecycleObserver(userLifecycleObserver: UserLifecycleObserver) {
        if (!userLifecycleObservers.contains(userLifecycleObserver)) {
            userLifecycleObservers.add(userLifecycleObserver)
        }
    }

    fun sendMessageWithButtonLink(message: String, chatId: Long, buttonText: String, buttonLink: String): Message? {
        val button = arrayListOf(listOf(BotButton(buttonText, buttonText, buttonLink)))
        return sendMessage(message, chatId, button, true, true)
    }

}