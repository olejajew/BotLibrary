package com.doblack.bot_library.analytics

import com.doblack.bot_library.DatabaseDelegate
import com.doblack.bot_library.analytics.messaging.MessagingProvider
import com.doblack.bot_library.analytics.models.MailingModel
import com.doblack.bot_library.analytics.referrer.ReferrerProvider
import com.doblack.bot_library.analytics.users.UsersProvider
import com.doblack.bot_library.FilesStorageDelegate
import com.doblack.bot_library.analytics.models.MailingMessageModel

class AnalyticsModule(
    private val analyticsBot: AnalyticsBot,
    private val databaseDelegate: DatabaseDelegate,
    private val filesStorageDelegate: FilesStorageDelegate,
    allowedReferrer: Boolean
) {

    private val messagingProvider = MessagingProvider(this)
    private val referrerProvider = ReferrerProvider(this, analyticsBot.getNewReferrerListener())
    private val usersProvider = UsersProvider(this)

    fun getMessagingProvider() = messagingProvider
    fun getReferrerProvider() = referrerProvider
    fun getUsersProvider() = usersProvider
    fun getChatBot() = analyticsBot.getChatBot()

    init {
        messagingProvider.init()
        if(allowedReferrer) {
            referrerProvider.init(allowedReferrer)
        }
        usersProvider.init()
        analyticsBot.getChatBot().addUserLifecycleObserver(usersProvider)
        analyticsBot.getChatBot().addUserLifecycleObserver(referrerProvider)
    }

    fun getFilesProvider() = filesStorageDelegate

    fun getDatabase() = databaseDelegate

    fun sendMessage(mailingModel: MailingModel, usersId: List<Long>){
        messagingProvider.sendMessage(mailingModel, usersId)
    }

    fun deleteMessage(mailingMessageModels: List<MailingMessageModel>){
        messagingProvider.deleteMessage(mailingMessageModels)
    }

    fun updateMessage(mailingModel: MailingModel, mailingMessageModels: List<MailingMessageModel>){
        messagingProvider.updateMessage(mailingModel, mailingMessageModels)
    }

}