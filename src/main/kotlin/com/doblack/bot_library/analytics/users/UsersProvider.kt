package com.doblack.bot_library.analytics.users

import com.doblack.bot_library.analytics.AnalyticsModule
import com.doblack.bot_library.analytics.users.data.DayUsersInfoModel
import com.doblack.bot_library.analytics.users.data.UsersCountModel
import com.doblack.bot_library.analytics.utils.groupByDay
import com.doblack.bot_library.base.UserLifecycleObserver
import org.apache.commons.logging.Log
import org.joda.time.DateTime
import org.joda.time.DateTimeFieldType
import org.telegram.telegrambots.meta.api.objects.Message
import java.util.concurrent.TimeUnit
import kotlin.collections.HashMap

class UsersProvider(private val analyticsModule: AnalyticsModule) : UserLifecycleObserver {

    private val aliveUserChecker = AliveUserChecker()

    fun init() {
        aliveUserChecker.init(analyticsModule)
        analyticsModule.getChatBot().addUserLifecycleObserver(this)
    }

    override fun onStartCommand(message: Message) {
        println("User started = ${message.chat}")
        analyticsModule.getDatabaseHelper().usersTableProvider.saveUser(message.chatId)
    }

    override fun onUserBlocked(chatId: Long) {
        analyticsModule.getDatabaseHelper().usersTableProvider.userBlocked(chatId)
    }

    fun getAliveUsers(): List<Long> {
        return analyticsModule.getDatabaseHelper().usersTableProvider.getAliveUsers().map { it.tgUserId }
    }

    fun getUsersAnalytics(from: Long, to: Long, s: Int): MutableCollection<DayUsersInfoModel> {
        val step = if (s > 0) {
            s
        } else {
            1
        }
        val dateFrom = DateTime(from)
        dateFrom.withField(DateTimeFieldType.hourOfDay(), 0)
        dateFrom.withField(DateTimeFieldType.minuteOfDay(), 0)

        val dateTo = DateTime(to)
        dateTo.withField(DateTimeFieldType.hourOfDay(), 23)
        dateTo.withField(DateTimeFieldType.minuteOfDay(), 59)
        return createStatisticsMap(from, to, step).values

    }

    private fun createStatisticsMap(from: Long, to: Long, step: Int): HashMap<Int, DayUsersInfoModel> {
        val usersAnalyticsDatabaseModel = analyticsModule.getDatabaseHelper().getUsersAnalytics(from, to)
        val resultMap = HashMap<Int, DayUsersInfoModel>()
        usersAnalyticsDatabaseModel?.total?.groupByDay(step, from) { day, count ->
            if (resultMap.contains(day)) {
                resultMap[day]!!.usersCountModel.totalUsers = count
            } else {
                resultMap[day] = DayUsersInfoModel(
                    from + TimeUnit.DAYS.toMillis((step * day).toLong()),
                    UsersCountModel(
                        count,
                        0,
                        0
                    )
                )
            }
        }
        usersAnalyticsDatabaseModel?.alive?.groupByDay(step, from) { day, count ->
            if (resultMap.contains(day)) {
                resultMap[day]!!.usersCountModel.aliveUsers = count
            } else {
                resultMap[day] = DayUsersInfoModel(
                    from + TimeUnit.DAYS.toMillis((step * day).toLong()),
                    UsersCountModel(
                        0,
                        count,
                        0
                    )
                )
            }
        }
        usersAnalyticsDatabaseModel?.referrers?.groupBy {
            (it - from) / TimeUnit.DAYS.toMillis(step.toLong())
        }?.forEach { (key, value) ->
            val day = key.toInt()
            if (resultMap.contains(day)) {
                resultMap[day]!!.usersCountModel.referrerUsers = value.size
            } else {
                resultMap[day] = DayUsersInfoModel(
                    from + TimeUnit.DAYS.toMillis((day * step).toLong()),
                    UsersCountModel(
                    0,
                    0,
                    value.size
                    )
                )
            }
        }
        return resultMap
    }

    fun getUsersCount(): UsersCountModel {
        return analyticsModule.getDatabaseHelper().getUserCount()
    }
}