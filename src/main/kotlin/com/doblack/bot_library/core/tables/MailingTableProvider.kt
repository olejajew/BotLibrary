package com.doblack.bot_library.core.tables

import com.doblack.bot_library.analytics.messaging.data.MailingModel
import com.google.cloud.firestore.CollectionReference
import com.google.cloud.firestore.Query
import java.lang.Exception
import java.util.*

class MailingTableProvider(private val subCollection: CollectionReference) {

    fun saveMailing(mailingModel: MailingModel): String {
        if(mailingModel.mailingId.isEmpty()){
            mailingModel.mailingId = UUID.randomUUID().toString()
        }
        subCollection.document(mailingModel.mailingId)
            .set(mailingModel)
        return mailingModel.mailingId
    }

    fun getMailings(): List<MailingModel> {
        return subCollection.whereLessThan(MailingModel::sentTime.name, System.currentTimeMillis())
            .get()
            .get()
            .toObjects(MailingModel::class.java)
    }

    fun getScheduledMailings(): List<MailingModel> {
        return subCollection.whereGreaterThan(MailingModel::sentTime.name, System.currentTimeMillis())
            .get()
            .get()
            .toObjects(MailingModel::class.java)
    }

    fun deleteMailing(mailingId: String) {
        subCollection.document(mailingId).delete()
    }

    fun editMailing(mailingModel: MailingModel) {
        subCollection.document(mailingModel.mailingId)
            .update(
                mapOf(
                    MailingModel::message.name to mailingModel.message,
                    MailingModel::images.name to mailingModel.images,
                    MailingModel::buttons.name to mailingModel.buttons
                )
            )
    }

    fun getMailing(id: String): MailingModel? {
        return try {
            subCollection.document(id).get().get().toObject(MailingModel::class.java)
        } catch (e: Exception) {
            null
        }
    }

    fun getNextScheduledMessage(): MailingModel? {
        return try {
            subCollection.whereGreaterThan(MailingModel::sentTime.name, System.currentTimeMillis())
                .orderBy(MailingModel::sentTime.name, Query.Direction.ASCENDING)
                .limit(1)
                .get()
                .get()
                .first()
                .toObject(MailingModel::class.java)
        } catch (e: Exception) {
            null
        }
    }

    fun editScheduledMailing(mailingModel: MailingModel) {
        if(mailingModel.mailingId == null){
            return
        }
        val updateInfo = hashMapOf<String, Any>()
        if(mailingModel.buttons.isNotEmpty()){
            updateInfo[MailingModel::buttons.name] = mailingModel.buttons
        }
        if(mailingModel.images.isNotEmpty()){
            updateInfo[MailingModel::images.name] = mailingModel.images
        }
        if(mailingModel.sentTime != 0L){
            updateInfo[MailingModel::sentTime.name] = mailingModel.sentTime
        }
        if(mailingModel.message.isNotEmpty()){
            updateInfo[MailingModel::message.name] = mailingModel.message
        }

        subCollection.document(mailingModel.mailingId!!)
            .update(updateInfo)
    }
}