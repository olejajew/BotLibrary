package com.doblack.bot_library.analytics.messaging

import com.doblack.bot_library.analytics.AnalyticsModule
import com.doblack.bot_library.analytics.models.MailingModel
import com.doblack.bot_library.analytics.messaging.helpers.SendMessageHelper
import com.doblack.bot_library.analytics.messaging.helpers.UpdateMessageHelper
import com.doblack.bot_library.base.UserLifecycleObserver
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.telegram.telegrambots.meta.api.objects.Update

class MessagingProvider(private val analyticsModule: AnalyticsModule) {

    private var messageScheduler = MessageScheduler(this)
    private var updateMessageHelper = UpdateMessageHelper(analyticsModule)
    private var sendMessageHelper = SendMessageHelper(analyticsModule)

    fun init() {
        analyticsModule.getChatBot().addUserLifecycleObserver(object : UserLifecycleObserver {
            override fun onStartCommand(update: Update) {

            }

            override fun onUserBlocked(chatId: Long) {
                analyticsModule.getDatabase().userBlocked(chatId)
            }

        })
        messageScheduler.init()
    }

    fun sendMessage(mailingModel: MailingModel) {
        sendMessageHelper.sendMessage(mailingModel)
    }

    fun getNextScheduledMessage(): MailingModel? {
        return analyticsModule.getDatabase().getNextScheduledMessage()
    }

    fun updatePlanningMessage(mailingId: String, sendingTime: Long) {
        messageScheduler.mailingChanged(mailingId, sendingTime)
    }

    fun deletePlanningMessage(mailingId: String) {
        messageScheduler.mailingDeleted(mailingId)
    }

    fun newPlanningMessage(date: Long) {
        messageScheduler.newMailingMessage(date)
    }

    fun updateMessage(mailingModel: MailingModel) {
        updateMessageHelper.updateMessage(mailingModel)
    }

    fun deleteMessage(mailingId: String) {
        val database = analyticsModule.getDatabase()
        runBlocking {
            launch {
                database.getMailingMessageIds(mailingId)
                    .forEach {
                        analyticsModule.getChatBot().deleteMessage(it.chat_id, it.message_id)
                    }
            }
        }
    }


}