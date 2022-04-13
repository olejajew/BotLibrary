package com.doblack.bot_library.analytics.messaging.helpers

import com.doblack.bot_library.analytics.AnalyticsModule
import com.doblack.bot_library.analytics.models.MailingMessageModel
import com.doblack.bot_library.analytics.models.MailingModel
import com.doblack.bot_library.base.models.ImageInputStream
import java.io.ByteArrayInputStream

class SendMessageHelper(private val analyticsModule: AnalyticsModule) {

    fun sendMessage(mailingModel: MailingModel) {
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
        val listMessageIds = mutableListOf<MailingMessageModel>()

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
                listMessageIds.add(
                    MailingMessageModel(
                        mailingModel.mailingId,
                        it,
                        message.messageId
                    )
                )
            }
        }
        analyticsModule.getDatabase().saveMailingMessageIds(listMessageIds)
    }

    private fun sendMessageWithImage(mailingModel: MailingModel) {
        val listMessageIds = mutableListOf<MailingMessageModel>()

        //todo Вот тут выходит что мы только одну картинку берем. Не камильфо
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
                listMessageIds.add(
                    MailingMessageModel(
                        mailingModel.mailingId,
                        it,
                        message.messageId
                    )
                )
            }
        }
        analyticsModule.getDatabase().saveMailingMessageIds(listMessageIds)
    }

    private fun sendMessageWithButtons(mailingModel: MailingModel) {
        val listMessageIds = mutableListOf<MailingMessageModel>()
        val listButtons = arrayListOf(mailingModel.buttons)
        analyticsModule.getUsersProvider().getAliveUsers().forEach {
            val message = analyticsModule.getChatBot().sendMessage(mailingModel.message, it, listButtons, true)
            if (message != null) {
                listMessageIds.add(
                    MailingMessageModel(
                        mailingModel.mailingId,
                        it,
                        message.messageId,
                    )
                )
            }
        }
        analyticsModule.getDatabase().saveMailingMessageIds(listMessageIds)
    }

    private fun sendTextMessage(mailingModel: MailingModel) {
        val list = mutableListOf<MailingMessageModel>()
        analyticsModule.getUsersProvider().getAliveUsers().forEach {
            val message = analyticsModule.getChatBot().sendMessage(mailingModel.message, it)
            if (message != null) {
                list.add(
                    MailingMessageModel(
                        mailingModel.mailingId,
                        it,
                        message.messageId
                    )
                )
            }
        }
        analyticsModule.getDatabase().saveMailingMessageIds(list)
    }

}