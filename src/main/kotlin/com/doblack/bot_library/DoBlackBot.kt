package com.doblack.bot_library

import com.doblack.bot_library.analytics.AnalyticsBot
import com.doblack.bot_library.analytics.AnalyticsModule
import com.doblack.bot_library.base.ChatBot
import com.doblack.bot_library.base.chatId
import com.doblack.bot_library.base.getCommand
import com.doblack.bot_library.constructor.BotConstructor
import com.doblack.bot_library.constructor.ConstructorModule
import org.telegram.telegrambots.meta.api.objects.Update

abstract class DoBlackBot() : ChatBot() {

    //todo Бля буду конфликт между mailin со стороны analytics и со стороны constructor

    var analyticsModule: AnalyticsModule? = null
    var constructorModule: ConstructorModule? = null

    fun initAnalyticsModule(
        analyticsBot: AnalyticsBot,
        databaseDelegate: DatabaseDelegate,
        filesStorageDelegate: FilesStorageDelegate,
    ) {
        analyticsModule = AnalyticsModule(
            analyticsBot,
            databaseDelegate,
            filesStorageDelegate,
            true
        )
    }

    fun initConstructorModule(botConstructor: BotConstructor, filesStorageDelegate: FilesStorageDelegate) {
        constructorModule = ConstructorModule(
            botConstructor,
            filesStorageDelegate
        )
    }

    override fun commandReceived(update: Update) {
        super.commandReceived(update)
        constructorModule?.onCommand(update.message.getCommand(), update.chatId())
        onCommandReceived(update)
    }

    abstract fun onCommandReceived(update: Update)

    override fun messageReceived(update: Update) {
        constructorModule?.onMessage(update.message.text, update.chatId())
        onMessageReceived(update)
    }

    abstract fun onMessageReceived(update: Update)

    override fun callbackMessageReceived(update: Update) {
        constructorModule?.callbackReceived(
            update.callbackQuery.data,
            update.callbackQuery.inlineMessageId,
            update.chatId()
        )
        onCallbackReceived(update)
    }

    abstract fun onCallbackReceived(update: Update)


}