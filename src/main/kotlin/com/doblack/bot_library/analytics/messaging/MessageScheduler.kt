package com.doblack.bot_library.analytics.messaging

import com.doblack.bot_library.analytics.models.MailingModel
import java.util.*

class MessageScheduler(private val messagingProvider: MessagingProvider) {

    private var timer: Timer? = null
    private var sendingTime: Long? = null
    private var mailingId: String? = null

    fun init() {
        sendingTime = null
        mailingId = null

        timer?.cancel()
        val nextPlanningMessage = messagingProvider.getNextScheduledMessage() ?: return
        sendingTime = nextPlanningMessage.sentTime
        mailingId = nextPlanningMessage.mailingId
        timer = Timer()
        timer!!.schedule(
            object : TimerTask() {
                override fun run() {
                    println("Send scheduling message")
                    //todo Вот тут надо собрать список всех id заблокированных пользователей и отдать их списком
                    messagingProvider.sendMessage(nextPlanningMessage)
                    init()
                }
            }, nextPlanningMessage.sentTime - System.currentTimeMillis()
        )
    }

    fun mailingChanged(mailingId: String, sendingTime: Long) {
        if (this.mailingId == mailingId || (this.sendingTime ?: 0) > sendingTime) {
            init()
        }
    }

    fun mailingDeleted(mailingId: String) {
        if (this.mailingId == mailingId) {
            init()
        }
    }

    fun newMailingMessage(date: Long) {
        if ((sendingTime ?: 0) > date) {
            init()
        }
    }

}