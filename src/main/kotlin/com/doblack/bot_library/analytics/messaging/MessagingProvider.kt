package com.doblack.bot_library.analytics.messaging

import com.doblack.bot_library.analytics.AnalyticsModule
import com.doblack.bot_library.analytics.messaging.data.MailingModel
import com.doblack.bot_library.analytics.messaging.data.PostSendMessageModel
import com.doblack.bot_library.analytics.messaging.data.UpdateMessageModel
import com.doblack.bot_library.analytics.messaging.data.UrlButtonModel
import com.doblack.bot_library.analytics.messaging.helpers.SendMessageHelper
import com.doblack.bot_library.analytics.messaging.helpers.UpdateMessageHelper
import com.doblack.bot_library.analytics.utils.ListUtils
import com.doblack.bot_library.base.UserLifecycleObserver
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking
import org.telegram.telegrambots.meta.api.objects.Message

class MessagingProvider(private val analyticsModule: AnalyticsModule) {

    private var messageScheduler = MessageScheduler(this)
    private var updateMessageHelper = UpdateMessageHelper(analyticsModule)
    private var sendMessageHelper = SendMessageHelper(analyticsModule)

    fun init() {
        analyticsModule.getChatBot().addUserLifecycleObserver(object : UserLifecycleObserver {
            override fun onStartCommand(message: Message) {

            }

            override fun onUserBlocked(chatId: Long) {
                analyticsModule.getDatabaseHelper().chatMailingTableProvider.removeMessageByUser(chatId)
            }

        })
        messageScheduler.init()
    }

    fun sendMessageToEverything(sendMessageModel: PostSendMessageModel) {
        val mailing = convertToMailing(sendMessageModel)
        sendMessageHelper.sendMessage(mailing)
    }

    fun sendMessage(mailingModel: MailingModel) {
        sendMessageHelper.sendMessage(mailingModel)
    }

    fun updateMessage(updateMessageModel: UpdateMessageModel) {
        updateMessageHelper.updateMessage(updateMessageModel)
    }

    fun deleteMessage(mailingId: String) {
        val databaseHelper = analyticsModule.getDatabaseHelper()
        if (analyticsModule.getDatabaseHelper().mailingTableProvider.getMailing(mailingId) == null) {
            return
        }
        val mailingModel = databaseHelper.mailingTableProvider.getMailing(mailingId) ?: return
        //todo Вот тут не оптимизированы запросы к бд
        runBlocking {
            launch {
                databaseHelper.chatMailingTableProvider.getChatMailingsId(mailingId)
                    .forEach {
                        analyticsModule.getChatBot().deleteMessage(it.chatId, it.messageId)
                    }
                databaseHelper.mailingTableProvider.deleteMailing(mailingId)
                databaseHelper.chatMailingTableProvider.deleteMailing(mailingId)

            }
        }
        if (!mailingModel.images.isEmpty()) {
            analyticsModule
                .getFilesProvider()
                .deleteImages(ListUtils.convertStringToList(mailingModel.images))
        }
    }

    fun getAllMailing(): List<PostSendMessageModel> {
        return analyticsModule.getDatabaseHelper().mailingTableProvider.getMailings()
            .map {
                PostSendMessageModel(
                    analyticsModule.getBotId(),
                    it.message,
                    ListUtils.convertStringToList(it.images).map {
                        analyticsModule.getFilesProvider().getImageLink(it)
                    },
                    ListUtils.convertStringToList(it.buttons).map {
                        UrlButtonModel(it)
                    },
                    it.sentTime,
                    it.mailingId
                )
            }
    }

    fun getNextScheduledMessage(): MailingModel? {
        return analyticsModule.getDatabaseHelper().mailingTableProvider.getNextScheduledMessage()
    }

    fun deletePlanningMessage(mailingId: String) {
        analyticsModule.getDatabaseHelper().mailingTableProvider.deleteMailing(mailingId)
        messageScheduler.init()
    }

    fun savePlanningMessage(sendMessageModel: PostSendMessageModel) {
        val mailingModel = convertToMailing(sendMessageModel)
        analyticsModule.getDatabaseHelper().mailingTableProvider.saveMailing(mailingModel)
        messageScheduler.init()
    }

    private fun convertToMailing(sendMessageModel: PostSendMessageModel): MailingModel {
        val images = if (sendMessageModel.imagesInBase64.isNullOrEmpty()) {
            ""
        } else {
            val item = sendMessageModel.imagesInBase64.first()
            val image = analyticsModule.getFilesProvider().saveImageFromBase64(item)
            ListUtils.convertListToString(listOf(image))
        }
        val buttons = if (sendMessageModel.buttons.isNullOrEmpty()) {
            ""
        } else {
            val list: List<UrlButtonModel> = sendMessageModel.buttons
            ListUtils.convertListToString(list.map { it.toString() })
        }
        //todo В параллель можно отправлять и сохранять
        return MailingModel(
            sendMessageModel.message,
            images,
            buttons = buttons,
            sentTime = sendMessageModel.sentTime ?: System.currentTimeMillis(),
            mailingId = sendMessageModel.id ?: ""
        )
    }

    private fun convertToMailing(sendMessageModel: UpdateMessageModel): MailingModel {
        val images = if (sendMessageModel.newImagesInBase64.isNullOrEmpty()) {
            ""
        } else {
            val item = sendMessageModel.newImagesInBase64.first()
            val image = analyticsModule.getFilesProvider().saveImageFromBase64(item)
            ListUtils.convertListToString(listOf(image))
        }
        val buttons = if (sendMessageModel.buttons.isNullOrEmpty()) {
            ""
        } else {
            val list: List<UrlButtonModel> = sendMessageModel.buttons
            ListUtils.convertListToString(list.map { it.toString() })
        }
        return MailingModel(
            sendMessageModel.message,
            images,
            buttons = buttons,
            sentTime = sendMessageModel.sentTime,
            mailingId = sendMessageModel.id
        )
    }

    fun getPlanningMailings(): Any {
        return analyticsModule.getDatabaseHelper().mailingTableProvider.getScheduledMailings()
            .map {
                PostSendMessageModel(
                    analyticsModule.getBotId(),
                    it.message,
                    ListUtils.convertStringToList(it.images).map {
                        analyticsModule.getFilesProvider().getImageLink(it)
                    },
                    ListUtils.convertStringToList(it.buttons).map { UrlButtonModel(it) },
                    it.sentTime,
                    it.mailingId
                )
            }
    }

    fun updatePlanningMessage(updateMessageModel: UpdateMessageModel) {
        val mailingModel = convertToMailing(updateMessageModel)
        analyticsModule.getDatabaseHelper().mailingTableProvider.editScheduledMailing(mailingModel)
        messageScheduler.init()
    }

}