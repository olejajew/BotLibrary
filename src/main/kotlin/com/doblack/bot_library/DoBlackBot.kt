package com.doblack.bot_library

import com.doblack.bot_library.analytics.AnalyticsBot
import com.doblack.bot_library.analytics.AnalyticsModule
import com.doblack.bot_library.base.ChatBot
import com.doblack.bot_library.base.chatId
import com.doblack.bot_library.base.getCommand
import com.doblack.bot_library.constructor.BotConstructor
import com.doblack.bot_library.constructor.ConstructorModule
import com.doblack.bot_library.core.AwsProvider
import com.doblack.bot_library.core.FirestoreProvider
import org.telegram.telegrambots.meta.api.objects.Message
import org.telegram.telegrambots.meta.api.objects.Update

abstract class DoBlackBot(
    private val botId: String,
    firestoreProvider: FirestoreProvider,
    awsProvider: AwsProvider
) : ChatBot(), AnalyticsBot,
    BotConstructor {

    //todo Бля буду конфликт между mailin со стороны analytics и со стороны constructor
    //todo Вынести строки для сообщений Reward и Referrer в отдельное место в настройках

    var analyticsModule: AnalyticsModule = AnalyticsModule(this, firestoreProvider, awsProvider)
    var constructorModule: ConstructorModule? = ConstructorModule(this, awsProvider)

    override fun documentReceived(message: Message) {

    }

    override fun photoReceived(message: Message) {

    }

    override fun commandReceived(message: Message) {
        super.commandReceived(message)
        constructorModule?.onCommand(message.getCommand(), message.chatId)
    }

    override fun messageReceived(message: Message) {
        constructorModule?.onMessage(message.text, message.chatId)
    }

    override fun callbackMessageReceived(update: Update) {
        constructorModule?.callbackReceived(
            update.callbackQuery.data,
            update.callbackQuery.inlineMessageId,
            update.chatId()
        )
    }

    override fun getChatBot(): ChatBot {
        return this
    }

    override fun getBotId(): String {
        return botId
    }

}