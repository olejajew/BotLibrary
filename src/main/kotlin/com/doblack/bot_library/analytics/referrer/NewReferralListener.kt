package com.doblack.bot_library.analytics.referrer

interface NewReferralListener {

    fun referralReward(userId: Long, reward: String?)

    fun referrerReward(userId: Long?, reward: String?)

}