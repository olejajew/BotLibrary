package com.doblack.bot_library.analytics

import com.doblack.bot_library.analytics.referrer.NewReferralListener
import com.doblack.bot_library.base.ChatBot

interface AnalyticsBot {

    fun getChatBot(): ChatBot

    fun getBotId(): String

    fun getNewReferrerListener(): NewReferralListener

}