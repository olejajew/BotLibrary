package com.doblack.bot_library.analytics.messaging

import com.doblack.bot_library.analytics.AnalyticsModule
import com.doblack.bot_library.analytics.models.MailingModel
import com.doblack.bot_library.analytics.messaging.helpers.SendMessageHelper
import com.doblack.bot_library.analytics.messaging.helpers.UpdateMessageHelper
import com.doblack.bot_library.analytics.models.MailingMessageModel
import com.doblack.bot_library.base.UserLifecycleObserver
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.telegram.telegrambots.meta.api.objects.Update

class MessagingProvider(private val analyticsModule: AnalyticsModule) {

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
    }

    fun sendMessage(mailingModel: MailingModel, usersId: List<Long>) {
        sendMessageHelper.sendMessage(mailingModel, usersId)
    }

    fun updateMessage(mailingModel: MailingModel, mailingMessageModels: List<MailingMessageModel>) {
        updateMessageHelper.updateMessage(mailingModel, mailingMessageModels)
    }

    fun deleteMessage(mailingMessageModels: List<MailingMessageModel>) {
        runBlocking {
            launch {
                mailingMessageModels
                    .forEach {
                        analyticsModule.getChatBot().deleteMessage(it.chat_id, it.message_id)
                    }
            }
        }
    }


}