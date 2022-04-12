package com.doblack.bot_library.analytics.users

import com.doblack.bot_library.analytics.AnalyticsModule
import com.doblack.bot_library.analytics.models.UserModel
import com.doblack.bot_library.base.UserLifecycleObserver
import com.doblack.bot_library.base.chatId
import org.telegram.telegrambots.meta.api.objects.Update

class UsersProvider(private val analyticsModule: AnalyticsModule) : UserLifecycleObserver {

    private val aliveUserChecker = AliveUserChecker()

    fun init() {
        aliveUserChecker.init(analyticsModule)
        analyticsModule.getChatBot().addUserLifecycleObserver(this)
    }

    override fun onStartCommand(update: Update) {
        println("User started = ${update.message.chatId}")
        analyticsModule.getDatabase().saveUser(
            UserModel(
                update.chatId(),
                System.currentTimeMillis(),
                true,
                null,
                update.message.from.userName
            )
        )
    }

    override fun onUserBlocked(chatId: Long) {
        analyticsModule.getDatabase().userBlocked(chatId)
    }

    fun getAliveUsers(): List<Long> {
        return analyticsModule.getDatabase().getAliveUsersId()
    }

}