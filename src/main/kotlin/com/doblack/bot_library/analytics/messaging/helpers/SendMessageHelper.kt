package com.doblack.bot_library.analytics.messaging.helpers

import com.doblack.bot_library.analytics.AnalyticsModule
import com.doblack.bot_library.analytics.models.MailingMessageModel
import com.doblack.bot_library.analytics.models.MailingModel
import com.doblack.bot_library.base.models.ImageInputStream
import java.io.ByteArrayInputStream

class SendMessageHelper(private val analyticsModule: AnalyticsModule) {

    fun sendMessage(mailingModel: MailingModel) {
        if (mailingModel.mailingId.isEmpty()) {
            analyticsModule.getDatabase().saveMailing(mailingModel)
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
        val imageName = mailingModel.images[0]
        val file = analyticsModule.getFilesProvider().getImageInputStream(imageName)
        val byteArray = file.readBytes()
        analyticsModule.getUsersProvider().getAliveUsers().forEach {
            val message =
                analyticsModule.getChatBot()
                    .sendMessageWithImageInputStream(
                        ImageInputStream(
                            ByteArrayInputStream(byteArray.clone()),
                            imageName
                        ), it, mailingModel.message, arrayListOf(mailingModel.buttons)
                    )

            if (message != null) {
                analyticsModule.getDatabase().saveMailingMessageId(
                    MailingMessageModel(
                        mailingModel.mailingId,
                        it,
                        message.messageId
                    )
                )
            }
        }
    }

    private fun sendMessageWithImage(mailingModel: MailingModel) {
        val imageName = mailingModel.images[0]
        val file = analyticsModule.getFilesProvider().getImageInputStream(imageName)
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
                analyticsModule.getDatabase().saveMailingMessageId(
                    MailingMessageModel(
                        mailingModel.mailingId,
                        it,
                        message.messageId
                    )
                )
            }
        }
    }

    private fun sendMessageWithButtons(mailingModel: MailingModel) {
        val listButtons = arrayListOf(mailingModel.buttons)
        analyticsModule.getUsersProvider().getAliveUsers().forEach {
            val message = analyticsModule.getChatBot().sendMessage(mailingModel.message, it, listButtons, true)
            if (message != null) {
                analyticsModule.getDatabase().saveMailingMessageId(
                    MailingMessageModel(
                        mailingModel.mailingId,
                        it,
                        message.messageId,
                    )
                )
            }
        }
    }

    private fun sendTextMessage(mailingModel: MailingModel) {
        analyticsModule.getUsersProvider().getAliveUsers().forEach {
            val message = analyticsModule.getChatBot().sendMessage(mailingModel.message, it)
            if (message != null) {
                analyticsModule.getDatabase().saveMailingMessageId(
                    MailingMessageModel(
                        mailingModel.mailingId,
                        it,
                        message.messageId
                    )
                )
            }
        }
    }

}