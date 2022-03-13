package com.doblack.bot_library.core.tables

import com.doblack.bot_library.analytics.referrer.data.ReferrerLinkModel
import com.google.cloud.firestore.CollectionReference
import com.google.cloud.firestore.FieldValue
import java.util.*

class ReferrersTableProvider(private val subCollection: CollectionReference) {

    fun checkExistReferrer(referrer: String): Boolean {
        return subCollection.whereEqualTo(
            ReferrerLinkModel::referrer.name, referrer
        ).limit(1)
            .get()
            .get()
            .count() > 0
    }

    fun saveReferrer(referrerLinkModel: ReferrerLinkModel) {
        subCollection.document(referrerLinkModel.referrerId)
            .set(referrerLinkModel)
    }

    fun updateReferrer(referrerLinkModel: ReferrerLinkModel) {
        subCollection.document(referrerLinkModel.referrerId)
            .update(
                mapOf(
                    ReferrerLinkModel::limit.name to referrerLinkModel.limit,
                    ReferrerLinkModel::reward.name to referrerLinkModel.reward,
                    ReferrerLinkModel::referrerName.name to referrerLinkModel.referrerName,
                )
            )
    }

    fun getReferrerInstructions(referrer: String): ReferrerLinkModel? {
        return try{
            subCollection.whereEqualTo(ReferrerLinkModel::referrer.name, referrer)
                .limit(1)
                .get()
                .get()
                .first()
                .toObject(ReferrerLinkModel::class.java)
        } catch (e: NoSuchElementException){
            return null
        }
    }

    fun getAllReferrerInstructions(): List<ReferrerLinkModel> {
        return subCollection.get().get().toObjects(ReferrerLinkModel::class.java)
    }

    fun deleteReferrerLink(referrerLinkId: String) {
        subCollection.document(referrerLinkId).delete()
    }

    fun newReferral(referrerId: String) {
        subCollection.document(referrerId)
            .update(ReferrerLinkModel::usedCount.name, FieldValue.increment(1))
    }

}