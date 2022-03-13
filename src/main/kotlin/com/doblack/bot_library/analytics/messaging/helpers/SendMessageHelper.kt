package com.doblack.bot_library.analytics.messaging.helpers

import com.doblack.bot_library.analytics.AnalyticsModule
import com.doblack.bot_library.analytics.messaging.data.ChatMailingModel
import com.doblack.bot_library.analytics.messaging.data.MailingModel
import com.doblack.bot_library.analytics.messaging.data.UrlButtonModel
import com.doblack.bot_library.analytics.utils.ListUtils
import com.doblack.bot_library.base.models.BotButton
import com.doblack.bot_library.base.models.ImageInputStream
import java.io.ByteArrayInputStream

class SendMessageHelper(private val analyticsModule: AnalyticsModule) {

    fun sendMessage(mailingModel: MailingModel) {
        if (mailingModel.mailingId.isEmpty()) {
            analyticsModule.getDatabaseHelper().mailingTableProvider.saveMailing(mailingModel)
        } else {
            mailingModel.mailingId
        }

        if (mailingModel.images.isNotEmpty()) {
            if (mailingModel.buttons.isNotEmpty()) {
                sendMessageWithImageAndButtons(mailingModel)
            } else {
                sendMessageWithImage(mailingModel)
            }
        } else if (mailingModel.buttons.isNotEmpty()) {
            sendMessageWithButtons(mailingModel)
        } else {
            sendTextMessage(mailingModel)
        }
    }

    private fun sendMessageWithImageAndButtons(mailingModel: MailingModel) {
        val chatMailingProvider = analyticsModule.getDatabaseHelper().chatMailingTableProvider
        val imageName = ListUtils.convertStringToList(mailingModel.images)[0]
        val buttonsStringList = ListUtils.convertStringToList(mailingModel.buttons)
        val buttons = buttonsStringList.map {
            val urlButtonModel = UrlButtonModel(it)
            BotButton(urlButtonModel.buttonText, "", urlButtonModel.buttonUrl)
        }
        val listButtons = arrayListOf(buttons)
        val file = analyticsModule.getFilesProvider().getImage(imageName)
        val byteArray = file.readBytes()
        analyticsModule.getUsersProvider().getAliveUsers().forEach {
            val message =
                analyticsModule.getChatBot()
                    .sendMessageWithImageInputStream(
                        ImageInputStream(
                            ByteArrayInputStream(byteArray.clone()),
                            imageName
                        ), it, mailingModel.message, listButtons
                    )

            if (message != null) {
                chatMailingProvider.saveMailingMessage(
                    ChatMailingModel(
                        mailingModel.mailingId,
                        it,
                        message.messageId
                    )
                )
            }
        }
    }

    private fun sendMessageWithImage(mailingModel: MailingModel) {
        val chatMailingProvider = analyticsModule.getDatabaseHelper().chatMailingTableProvider
        val imageName = ListUtils.convertStringToList(mailingModel.images)[0]
        val file = analyticsModule.getFilesProvider().getImage(imageName)
        val byteArray = file.readBytes()
        analyticsModule.getUsersProvider().getAliveUsers().forEach {
            val imageInputStream = ImageInputStream(
                ByteArrayInputStream(byteArray.clone()),
                imageName
            )
            val message = analyticsModule.getChatBot().sendMessageWithImageInputStream(
                imageInputStream,
                it,
                mailingModel.message
            )
            if (message != null) {
                chatMailingProvider.saveMailingMessage(
                    ChatMailingModel(
                        mailingModel.mailingId,
                        it,
                        message.messageId
                    )
                )
            }
        }
    }

    private fun sendMessageWithButtons(mailingModel: MailingModel) {
        val chatMailingProvider = analyticsModule.getDatabaseHelper().chatMailingTableProvider
        val buttonsStringList = ListUtils.convertStringToList(mailingModel.buttons)
        val buttons = buttonsStringList.map {
            val urlButtonModel = UrlButtonModel(it)
            BotButton(urlButtonModel.buttonText, "", urlButtonModel.buttonUrl)
        }
        val listButtons = arrayListOf(buttons)
        analyticsModule.getUsersProvider().getAliveUsers().forEach {
            val message = analyticsModule.getChatBot().sendMessage(mailingModel.message, it, listButtons, true)
            if (message != null) {
                chatMailingProvider.saveMailingMessage(
                    ChatMailingModel(
                        mailingModel.mailingId,
                        it,
                        message.messageId,
                    )
                )
            }
        }
    }

    private fun sendTextMessage(mailingModel: MailingModel) {
        val chatMailingProvider = analyticsModule.getDatabaseHelper().chatMailingTableProvider
        analyticsModule.getUsersProvider().getAliveUsers().forEach {
            val message = analyticsModule.getChatBot().sendMessage(mailingModel.message, it)
            if (message != null) {
                chatMailingProvider.saveMailingMessage(
                    ChatMailingModel(
                        mailingModel.mailingId,
                        it,
                        message.messageId
                    )
                )
            }
        }
    }

}