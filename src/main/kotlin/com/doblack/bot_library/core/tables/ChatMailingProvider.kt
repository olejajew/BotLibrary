package com.doblack.bot_library.core.tables

import com.doblack.bot_library.analytics.messaging.data.ChatMailingModel
import com.google.cloud.firestore.CollectionReference

class ChatMailingProvider(private val subCollection: CollectionReference) {

    fun saveMailingMessage(chatMailingModel: ChatMailingModel) {
        subCollection.document(chatMailingModel.mailingId)
            .set(chatMailingModel)
    }

    fun removeMessageByUser(chatId: Long) {
        subCollection.document(chatId.toString())
            .delete()
    }

    fun getChatMailingsId(mailingId: String): List<ChatMailingModel> {
        return subCollection.whereEqualTo(ChatMailingModel::mailingId.name, mailingId)
            .get()
            .get()
            .toObjects(ChatMailingModel::class.java)
    }

    fun deleteMailing(mailingId: String) {
        subCollection
            .whereEqualTo(ChatMailingModel::mailingId.name, mailingId)
            .get()
            .get()
            .forEach {
                it.reference.delete()
            }
    }

}