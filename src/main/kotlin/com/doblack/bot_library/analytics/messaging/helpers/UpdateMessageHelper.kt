package com.doblack.bot_library.analytics.messaging.helpers

import com.doblack.bot_library.analytics.AnalyticsModule
import com.doblack.bot_library.analytics.models.MailingModel
import com.doblack.bot_library.base.models.ImageInputStream
import kotlinx.coroutines.launch
import kotlinx.coroutines.runBlocking

class UpdateMessageHelper(private val analyticsModule: AnalyticsModule) {

    fun updateMessage(mailingModel: MailingModel) {
        val sentMessage = analyticsModule.getDatabase().getMailing(mailingModel.mailingId)
            ?: return
        when {
            mailingModel.images.isNotEmpty() -> {
                updateMessageWithImage(mailingModel)
            }
            sentMessage.images.isNotEmpty() -> {
                updateMessageCaption(mailingModel)
            }
            else -> {
                updateMessageTextAndButtons(mailingModel)
            }
        }
    }

    private fun updateMessageCaption(mailingModel: MailingModel) {
        val buttons = mailingModel.buttons
        val buttonsList = if (buttons.isNotEmpty()) {
            listOf(buttons)
        } else {
            null
        }
        runBlocking {
            launch {
                analyticsModule.getDatabase().getMailingMessageIds(mailingModel.mailingId)
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

    private fun updateMessageTextAndButtons(mailingModel: MailingModel) {
        runBlocking {
            launch {
                analyticsModule.getDatabase().getMailingMessageIds(mailingModel.mailingId)
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

    private fun updateMessageWithImage(mailingModel: MailingModel) {
        val image = mailingModel.images[0]
        val file = analyticsModule.getFilesProvider().getImageInputStream(image)
        val imageInputStream = ImageInputStream(
            file,
            image
        )
        runBlocking {
            launch {
                analyticsModule.getDatabase().getMailingMessageIds(mailingModel.mailingId)
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