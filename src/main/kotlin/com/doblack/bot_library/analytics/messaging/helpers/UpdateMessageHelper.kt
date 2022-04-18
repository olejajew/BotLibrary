package com.doblack.bot_library.analytics.messaging.helpers

import com.doblack.bot_library.analytics.AnalyticsModule
import com.doblack.bot_library.analytics.models.MailingMessageModel
import com.doblack.bot_library.analytics.models.MailingModel
import com.doblack.bot_library.base.models.ImageInputStream
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class UpdateMessageHelper(private val analyticsModule: AnalyticsModule) {

    fun updateMessage(mailingModel: MailingModel, mailingMessageModels: List<MailingMessageModel>) {
        val sentMessage = analyticsModule.getDatabase().getMailing(mailingModel.mailingId)
            ?: return
        when {
            mailingModel.images.isNotEmpty() -> {
                updateMessageWithImage(mailingModel, mailingMessageModels)
            }
            sentMessage.images.isNotEmpty() -> {
                updateMessageCaption(mailingModel, mailingMessageModels)
            }
            else -> {
                updateMessageTextAndButtons(mailingModel, mailingMessageModels)
            }
        }
    }

    private fun updateMessageCaption(mailingModel: MailingModel, mailingMessageModels: List<MailingMessageModel>) {
        val buttons = mailingModel.buttons
        val buttonsList = if (buttons.isNotEmpty()) {
            listOf(buttons)
        } else {
            null
        }
        runBlocking {
            launch {
                mailingMessageModels
                    .forEach {
                        analyticsModule.getChatBot().editMessageCaption(
                            it.chat_id,
                            it.message_id,
                            mailingModel.message,
                            buttonsList
                        )
                    }
            }
        }
    }

    private fun updateMessageTextAndButtons(mailingModel: MailingModel, mailingMessageModels: List<MailingMessageModel>) {
        runBlocking {
            launch {
                mailingMessageModels
                    .forEach {
                        analyticsModule.getChatBot().editMessage(
                            it.chat_id,
                            it.message_id,
                            mailingModel.message,
                            listOf(mailingModel.buttons)
                        )
                    }
            }
        }
    }

    private fun updateMessageWithImage(mailingModel: MailingModel, mailingMessageModels: List<MailingMessageModel>) {
        val image = mailingModel.images[0]
        val file = analyticsModule.getFilesProvider().getImageInputStream(image)
        val imageInputStream = ImageInputStream(
            file,
            image
        )
        runBlocking {
            launch {
                mailingMessageModels
                    .forEach {
                        analyticsModule.getChatBot().editMessageWithImageInputStream(
                            it.chat_id,
                            it.message_id,
                            mailingModel.message,
                            imageInputStream,
                            listOf(mailingModel.buttons)
                        )
                    }
            }
        }
    }

}