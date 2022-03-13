package com.doblack.bot_library.analytics.referrer

import com.doblack.bot_library.analytics.AnalyticsModule
import com.doblack.bot_library.analytics.referrer.data.ReferrerLinkModel
import com.doblack.bot_library.analytics.referrer.data.ReferrerPair
import com.doblack.bot_library.analytics.referrer.data.UserReferrerInfo
import com.doblack.bot_library.base.UserLifecycleObserver
import com.doblack.bot_library.base.getReferrerIfHas
import org.telegram.telegrambots.meta.api.objects.Message

class ReferrerProvider(
    private val analyticsModule: AnalyticsModule,
    private val newReferralListener: NewReferralListener
) : UserLifecycleObserver {

    private var allowedAnyReferrers: Boolean = false

    companion object {
        const val REFERRER_REWARD = "referrer_reward"
        const val REFERRAL_REWARD = "referral_reward"
    }

    fun init(allowedAnyReferrers: Boolean) {
        analyticsModule.getChatBot().addUserLifecycleObserver(this)
        this.allowedAnyReferrers = allowedAnyReferrers
    }

    override fun onStartCommand(message: Message) {
        val referrer = message.getReferrerIfHas() ?: return
        val referrerPair = ReferrerPair(referrer, message.chatId, System.currentTimeMillis())
        if (analyticsModule.getDatabaseHelper().referrerPairsTableProvider.checkHasReferrer(referrerPair)) {
            return
        }
        val userReferrer = !analyticsModule.getDatabaseHelper().referrersTableProvider.checkExistReferrer(referrer)
        referrerReceived(referrerPair, userReferrer)

    }

    private fun referrerReceived(referrerPair: ReferrerPair, userReferrer: Boolean) {
        analyticsModule.getDatabaseHelper().referrerPairsTableProvider.saveReferrerPaid(referrerPair)
        if (userReferrer) {
            newReferralListener.referralReward(referrerPair.referralId, getUserReferralReward())
            newReferralListener.referrerReward(referrerPair.referrer.toLongOrNull(), getUserReferrerReward())
        } else {
            val referrerInstructions =
                analyticsModule.getDatabaseHelper().referrersTableProvider.getReferrerInstructions(referrerPair.referrer)
            if (referrerInstructions != null && (referrerInstructions.limit > referrerInstructions.usedCount || referrerInstructions.limit == -1)) {
                newReferralListener.referralReward(referrerPair.referralId, referrerInstructions.reward)
                analyticsModule.getDatabaseHelper().referrersTableProvider.newReferral(referrerInstructions.referrerId)
            }
        }
    }

    override fun onUserBlocked(chatId: Long) {

    }

    fun saveReferrerLink(referrerLinkModel: ReferrerLinkModel) {
        analyticsModule.getDatabaseHelper().referrersTableProvider.saveReferrer(referrerLinkModel)
    }

    fun updateReferrerLink(referrerLinkModel: ReferrerLinkModel) {
        analyticsModule.getDatabaseHelper().referrersTableProvider.updateReferrer(referrerLinkModel)
    }

    fun getUserReferrerReward() = analyticsModule
        .getDatabaseHelper()
        .getBotPreferencesString(REFERRER_REWARD)

    fun getUserReferralReward() = analyticsModule
        .getDatabaseHelper()
        .getBotPreferencesString(REFERRAL_REWARD)

    fun getAllReferrerLinks(): List<ReferrerLinkModel> {
        return analyticsModule.getDatabaseHelper().referrersTableProvider
            .getAllReferrerInstructions()
    }

    fun deleteReferrerLink(referrerLinkId: String) {
        analyticsModule.getDatabaseHelper().referrersTableProvider.deleteReferrerLink(referrerLinkId)
    }

    fun getReferrerInstructions(referrerName: String) =
        analyticsModule.getDatabaseHelper().referrersTableProvider.getReferrerInstructions(referrerName)

    fun updateUserReferrerInfo(userReferrerInfo: UserReferrerInfo) {
        analyticsModule.getDatabaseHelper()
            .updatePreferences(userReferrerInfo)
    }

}