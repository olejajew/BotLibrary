package com.doblack.bot_library.analytics.utils

import com.doblack.bot_library.analytics.database.models.UsersAnalyticsDatabaseModel
import com.doblack.bot_library.analytics.users.data.User
import java.util.*
import java.util.concurrent.TimeUnit

object Emulate {

    fun createDatabaseUsers(from: Long, to: Long): UsersAnalyticsDatabaseModel {
        val days = (to - from) / TimeUnit.DAYS.toMillis(1)
        val users = arrayListOf<User>()
        val referrerTimes = arrayListOf<Long>()
        for (i in 0 until days) {
            for (z in 1 until i) {
                users.add(
                    User(
                        0,
                        from + TimeUnit.DAYS.toMillis(i),
                        Random().nextBoolean()
                    )
                )
                if (java.util.Random().nextBoolean()) {
                    referrerTimes.add(from + TimeUnit.DAYS.toMillis(i))
                }
            }
        }
        return UsersAnalyticsDatabaseModel(
            users,
            users.filter { it.alive },
            referrerTimes
        )
    }
}