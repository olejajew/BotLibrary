package com.doblack.bot_library.core

import com.botlibrary.core.FirestoreProvider
import com.doblack.bot_library.analytics.database.models.PreferencesModel
import com.doblack.bot_library.analytics.database.models.UsersAnalyticsDatabaseModel
import com.doblack.bot_library.analytics.referrer.data.UserReferrerInfo
import com.doblack.bot_library.analytics.users.data.UsersCountModel
import com.doblack.bot_library.core.tables.*
import com.google.cloud.firestore.CollectionReference
import com.google.cloud.firestore.SetOptions

class DatabaseHelper(val botId: String, private val debug: Boolean) {

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
    }

    private fun getSubCollection(collection: String): CollectionReference {
        return FirestoreProvider.getSubCollection(
            COLLECTION_NAME,
            botId,
            collection
        )
    }

    fun getBotPreferencesString(fieldName: String): String? {
        return FirestoreProvider.getDatabaseInstance()
            .collection(botId)
            .document(PREFERENCES_FILE)
            .get()
            .get()
            .getString(fieldName)
    }

    fun updatePreferences(userReferrerInfo: UserReferrerInfo) {
        //todo Да ты, я смотрю, хардкодный парень
        FirestoreProvider.getCollection("bots")
            .document(botId)
            .set(PreferencesModel(userReferrerInfo), SetOptions.merge())
    }

    fun getUsersAnalytics(from: Long, to: Long): UsersAnalyticsDatabaseModel? {
        //todo Новый имплемент нуежн
        return null
    }

    fun getUserCount(): UsersCountModel {
        return if (debug) {
            UsersCountModel(
                kotlin.random.Random.nextInt(2000, 3000),
                kotlin.random.Random.nextInt(1000, 2000),
                kotlin.random.Random.nextInt(1000)
            )
        } else {
            UsersCountModel(
                usersTableProvider.getAllUsersCount(),
                usersTableProvider.getAliveUsersCount(),
                referrerPairsTableProvider.getAllReferralCount()
            )
        }
    }
}