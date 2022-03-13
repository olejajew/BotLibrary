package com.doblack.bot_library.core

import com.doblack.bot_library.analytics.database.models.PreferencesModel
import com.doblack.bot_library.analytics.database.models.UsersAnalyticsDatabaseModel
import com.doblack.bot_library.analytics.referrer.data.UserReferrerInfo
import com.doblack.bot_library.analytics.users.data.UsersCountModel
import com.doblack.bot_library.core.tables.*
import com.google.cloud.firestore.CollectionReference
import com.google.cloud.firestore.SetOptions

class DatabaseHelper(val botId: String, private val firestoreProvider: FirestoreProvider) {

    //todo Перенести через interface

    val usersTableProvider = UsersTableProvider(getSubCollection(USERS_COLLECTION))
    val referrersTableProvider = ReferrersTableProvider(getSubCollection(REFERRERS_COLLECTION))
    val referrerPairsTableProvider = ReferrerPairsTableProvider(getSubCollection(REFERRERS_PAIR_COLLECTION))
    val mailingTableProvider = MailingTableProvider(getSubCollection(MAILING_COLLECTION))
    val chatMailingTableProvider = ChatMailingProvider(getSubCollection(CHAT_MAILING_COLLECTION))

    companion object {
        private const val COLLECTION_NAME = "analytics"
        private const val USERS_COLLECTION = "users"
        private const val REFERRERS_COLLECTION = "referrers"
        private const val REFERRERS_PAIR_COLLECTION = "referrersPair"
        private const val MAILING_COLLECTION = "mailing"
        private const val CHAT_MAILING_COLLECTION = "chatMailing"
        private const val PREFERENCES_FILE = "preferences"
        private const val BOT_PREFERENCES_COLLECTION = "bots"
    }

    private fun getSubCollection(collection: String): CollectionReference {
        return firestoreProvider.getSubCollection(
            COLLECTION_NAME,
            botId,
            collection
        )
    }

    fun getBotPreferencesString(fieldName: String): String? {
        return firestoreProvider.getDatabaseInstance()
            .collection(botId)
            .document(PREFERENCES_FILE)
            .get()
            .get()
            .getString(fieldName)
    }

    fun updatePreferences(userReferrerInfo: UserReferrerInfo) {
        firestoreProvider.getCollection(BOT_PREFERENCES_COLLECTION)
            .document(botId)
            .set(PreferencesModel(userReferrerInfo), SetOptions.merge())
    }

    fun getUsersAnalytics(from: Long, to: Long): UsersAnalyticsDatabaseModel? {
        //todo Новый имплемент нуежн
        return null
    }

    fun getUserCount(): UsersCountModel {
        return UsersCountModel(
            usersTableProvider.getAllUsersCount(),
            usersTableProvider.getAliveUsersCount(),
            referrerPairsTableProvider.getAllReferralCount()
        )
    }
}