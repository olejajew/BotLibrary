package com.doblack.bot_library.analytics.referrer

import com.doblack.bot_library.analytics.AnalyticsModule
import com.doblack.bot_library.analytics.models.ReferrerPair
import com.doblack.bot_library.base.UserLifecycleObserver
import com.doblack.bot_library.base.chatId
import com.doblack.bot_library.base.getReferrerIfHas
import org.telegram.telegrambots.meta.api.objects.Update

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

    override fun onStartCommand(update: Update) {
        val referrer = update.message.getReferrerIfHas() ?: return
        val referrerPair = ReferrerPair(referrer, update.chatId(), System.currentTimeMillis())
        if (analyticsModule.getDatabase().checkAlreadyReferrer(referrerPair)) {
            return
        }
        val userReferrer = !analyticsModule.getDatabase().checkExistReferrer(referrer)
        referrerReceived(referrerPair, userReferrer)

    }

    private fun referrerReceived(referrerPair: ReferrerPair, userReferrer: Boolean) {
        analyticsModule.getDatabase().newReferral(referrerPair)
        if (userReferrer) {
            newReferralListener.referralReward(referrerPair.referralId, getUserReferralReward())
            newReferralListener.referrerReward(referrerPair.referrer.toLongOrNull(), getUserReferrerReward())
        } else {
            val referrerInstructions =
                analyticsModule.getDatabase().getReferrerInstructions(referrerPair.referrer)
            if (referrerInstructions != null && (referrerInstructions.limit > referrerInstructions.usedCount || referrerInstructions.limit == -1)) {
                newReferralListener.referralReward(referrerPair.referralId, referrerInstructions.reward)
                analyticsModule.getDatabase().newReferral(
                    ReferrerPair(
                        referrerInstructions.referrerId,
                        referrerPair.referralId,
                        referrerPair.date
                    )
                )
            }
        }
    }

    override fun onUserBlocked(chatId: Long) {

    }

    private fun getUserReferrerReward() = analyticsModule
        .getDatabase()
        .getBotPreferencesString(REFERRER_REWARD)

    private fun getUserReferralReward() = analyticsModule
        .getDatabase()
        .getBotPreferencesString(REFERRAL_REWARD)

}