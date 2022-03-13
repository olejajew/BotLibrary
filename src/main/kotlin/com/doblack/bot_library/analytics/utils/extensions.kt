package com.doblack.bot_library.analytics.utils

import com.doblack.bot_library.analytics.users.data.User
import java.util.concurrent.TimeUnit

fun List<User>.groupByDay(step: Int, timeFrom: Long, onResult: (Int, Int) -> Unit) {
    groupBy {
        (it.createdTime - timeFrom) / TimeUnit.DAYS.toMillis(step.toLong())
    }.forEach { t, u ->
        onResult(t.toInt(), u.size)
    }
}