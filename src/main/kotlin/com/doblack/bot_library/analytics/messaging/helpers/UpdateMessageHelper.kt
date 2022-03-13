package com.doblack.bot_library.analytics.messaging.helpers

import com.doblack.bot_library.analytics.AnalyticsModule
import com.doblack.bot_library.analytics.messaging.data.MailingModel
import com.doblack.bot_library.analytics.messaging.data.UpdateMessageModel
import com.doblack.bot_library.analytics.messaging.data.UrlButtonModel
import com.doblack.bot_library.analytics.utils.ListUtils
import com.doblack.bot_library.base.models.BotButton
import com.doblack.bot_library.base.models.ImageInputStream
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class UpdateMessageHelper(private val analyticsModule: AnalyticsModule) {

    fun updateMessage(updateMessageModel: UpdateMessageModel) {
        val sentMessage = analyticsModule.getDatabaseHelper().mailingTableProvider.getMailing(updateMessageModel.id)
            ?: return
        when {
            updateMessageModel.newImagesInBase64 != null -> {
                updateMessageWithImage(updateMessageModel)
            }
            sentMessage.images.isNotEmpty() -> {
                updateMessageCaption(updateMessageModel, sentMessage)
            }
            else -> {
                updateMessageTextAndButtons(updateMessageModel, sentMessage)
            }
        }
    }

    private fun updateMessageCaption(updateMessageModel: UpdateMessageModel, sentMessage: MailingModel) {
        val buttons = updateMessageModel.buttons?.map {
            BotButton(it.buttonText, "", it.buttonUrl)
        }
        val buttonsList = if (buttons != null) {
            listOf(buttons)
        } else {
            null
        }
        val databaseHelper = analyticsModule.getDatabaseHelper()
        runBlocking {
            launch {
                databaseHelper.chatMailingTableProvider.getChatMailingsId(updateMessageModel.id)
                    .forEach {
                        analyticsModule.getChatBot().editMessageCaption(
                            it.chatId,
                            it.messageId,
                            updateMessageModel.message,
                            buttonsList
                        )
                    }
            }
        }
        val mailingModel = MailingModel(
            updateMessageModel.message,
            sentMessage.images,
            ListUtils.convertListToString(
                buttons?.map {
                    UrlButtonModel(it.title, it.url!!)
                        .toString()
                } ?: emptyList()),
            updateMessageModel.id
        )
        databaseHelper.mailingTableProvider.editMailing(mailingModel)
    }

    private fun updateMessageTextAndButtons(updateMessageModel: UpdateMessageModel, sentMessage: MailingModel) {
        val buttons = updateMessageModel.buttons?.map {
            BotButton(it.buttonText, "", it.buttonUrl)
        }
        val buttonsList = if (buttons != null) {
            listOf(buttons)
        } else {
            null
        }
        val databaseHelper = analyticsModule.getDatabaseHelper()
        runBlocking {
            launch {
                databaseHelper.chatMailingTableProvider.getChatMailingsId(updateMessageModel.id)
                    .forEach {
                        analyticsModule.getChatBot().editMessage(
                            it.chatId,
                            it.messageId,
                            updateMessageModel.message,
                            buttonsList
                        )
                    }
            }
        }
        val mailingModel = MailingModel(
            updateMessageModel.message,
            sentMessage.images,
            ListUtils.convertListToString(
                buttons?.map {
                    UrlButtonModel(it.title, it.url!!)
                        .toString()
                } ?: emptyList()),
            updateMessageModel.id
        )
        databaseHelper.mailingTableProvider.editMailing(mailingModel)
    }

    private fun updateMessageWithImage(updateMessageModel: UpdateMessageModel) {
        //todo Вот тут надо удалять старые картинки
        val image = analyticsModule.getFilesProvider().saveImageFromBase64(updateMessageModel.newImagesInBase64!![0])
        val file = analyticsModule.getFilesProvider().getImage(image)
        val buttons = updateMessageModel.buttons?.map {
            BotButton(it.buttonText, "", it.buttonUrl)
        }
        val buttonsList = if (buttons != null) {
            listOf(buttons)
        } else {
            null
        }
        val imageInputStream = ImageInputStream(
            file,
            image
        )
        val databaseHelper = analyticsModule.getDatabaseHelper()
        runBlocking {
            launch {
                databaseHelper.chatMailingTableProvider.getChatMailingsId(updateMessageModel.id)
                    .forEach {
                        analyticsModule.getChatBot().editMessageWithImageInputStream(
                            it.chatId,
                            it.messageId,
                            updateMessageModel.message,
                            imageInputStream,
                            buttonsList
                        )
                    }
            }
        }
        val mailingModel = MailingModel(
            updateMessageModel.message,
            ListUtils.convertListToString(listOf(image)),
            ListUtils.convertListToString(
                buttons?.map { UrlButtonModel(it.title, it.url ?: "").toString() } ?: emptyList()
            ),
            updateMessageModel.id
        )
        databaseHelper.mailingTableProvider.editMailing(mailingModel)
    }

}