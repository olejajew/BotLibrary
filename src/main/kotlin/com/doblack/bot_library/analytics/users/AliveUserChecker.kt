package com.doblack.bot_library.analytics.users

import com.doblack.bot_library.analytics.AnalyticsModule
import com.doblack.bot_library.base.checkIsBlocked
import kotlinx.coroutines.*
import org.telegram.telegrambots.meta.exceptions.TelegramApiException
import java.lang.Exception
import java.util.*
import java.util.concurrent.TimeUnit

class AliveUserChecker(private val hoursToCheck: Int = 23) {

    var timer: Timer? = null

    fun init(analyticsModule: AnalyticsModule) {
        runBlocking {
            launch {
                timer?.cancel()
                timer = null
                timer = Timer()
                timer!!.scheduleAtFixedRate(object : TimerTask() {
                    override fun run() {
                        analyticsModule.getDatabaseHelper().usersTableProvider.getAliveUsers().forEach {
                            try {
                                analyticsModule.getChatBot().tryChatAction(it.tgUserId)
                            } catch (e: TelegramApiException) {
                                if (e.checkIsBlocked()) {
                                    analyticsModule.getDatabaseHelper().usersTableProvider.userBlocked(it.tgUserId)
                                }
                            }
                        }
                    }
                }, getTimeToMidnight(), TimeUnit.DAYS.toMillis(1))
            }
        }
    }

    private fun getTimeToMidnight(): Long {
        val currentDate = Calendar.getInstance()
        currentDate.set(Calendar.HOUR_OF_DAY, hoursToCheck)
        currentDate.set(Calendar.MINUTE, 59)
        return currentDate.timeInMillis - System.currentTimeMillis()
    }


}