package com.doblack.bot_library.analytics.database.models

import com.doblack.bot_library.analytics.users.data.User

data class UsersAnalyticsDatabaseModel(
    val total: List<User>,
    val alive: List<User>,
    val referrers: List<Long>
)
