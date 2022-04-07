package com.doblack.bot_library

import com.doblack.bot_library.analytics.models.MailingMessageModel
import com.doblack.bot_library.analytics.models.MailingModel
import com.doblack.bot_library.analytics.models.ReferrerLinkModel
import com.doblack.bot_library.analytics.models.ReferrerPair
import com.doblack.bot_library.analytics.models.UserModel

interface DatabaseDelegate {

    fun getBotPreferencesString(fieldName: String): String?

    fun userBlocked(chatId: Long)

    fun getMailing(mailingId: String): MailingModel?

    fun getNextScheduledMessage(): MailingModel?

    fun saveMailing(mailingModel: MailingModel) {}
    fun saveMailingMessageId(mailingMessageModel: MailingMessageModel)
    fun getMailingMessageIds(id: String): List<MailingMessageModel>
    fun checkAlreadyReferrer(referrerPair: ReferrerPair): Boolean
    fun checkExistReferrer(referrer: String): Boolean
    fun newReferral(referrerPair: ReferrerPair)
    fun getReferrerInstructions(referrer: String): ReferrerLinkModel?
    fun getAliveUsers(): List<UserModel>
    fun saveUser(userModel: UserModel)
    fun getAliveUsersId(): List<Long>

}