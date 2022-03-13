package com.doblack.bot_library.analytics.messaging

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
                    messagingProvider.sendMessage(nextPlanningMessage)
                    init()
                }
            }, nextPlanningMessage.sentTime - System.currentTimeMillis()
        )
    }

}